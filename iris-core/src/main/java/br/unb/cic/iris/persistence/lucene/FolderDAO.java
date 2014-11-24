package br.unb.cic.iris.persistence.lucene;

import java.io.IOException;
import java.util.UUID;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;

import br.unb.cic.iris.core.exception.DBException;
import br.unb.cic.iris.core.model.EmailMessage;
import br.unb.cic.iris.core.model.FolderContent;
import br.unb.cic.iris.core.model.IrisFolder;
import br.unb.cic.iris.persistence.IFolderDAO;

public class FolderDAO implements IFolderDAO {

	private static FolderDAO instance;
	
	private FolderDAO() {}
	
	public static FolderDAO instance() {
		if (instance == null)
			instance = new FolderDAO();
		return instance;
	}

	/**
	 * This function returns only the folder, with no children.
	 * 
	 * @param folderId The id of the folder.
	 * @return The folder.
	 * @throws DBException 
	 */
	public IrisFolder findById(String folderId) throws DBException {
		IrisFolder folder = null;
		try {
			Query typeQuery = new TermQuery(new Term("type", "irisFolder"));
			Query idQuery = new TermQuery(new Term("id", folderId));

			// Checks whether a folder with the given 'name' exists in the index.
			BooleanQuery q = new BooleanQuery();
			q.add(new BooleanClause(typeQuery, Occur.MUST));
			q.add(new BooleanClause(idQuery, Occur.MUST));

			IndexSearcher searcher = IndexManager.getSearcher();
			TopDocs docs = searcher.search(q, 1);

			if (docs.totalHits > 0) {
				int docId = docs.scoreDocs[0].doc;
				folder = fromLuceneDoc(searcher.doc(docId));
			}

		} catch (Exception e) {
			throw new DBException("Folder '" + folderId + "' not found.", e);
		}

		return folder;
	}
	
	/**
	 * This function returns the folder and all of its children in an eager fashion.
	 * 
	 * @param folderName The name of the folder.
	 * @return The first folder found with the given name.
	 * @throws DBException
	 */
	public IrisFolder findByName(String folderName) throws DBException {
		IrisFolder rootFolder = null;
		try {
			Query typeQuery = new TermQuery(new Term("type", "irisFolder"));
			Query nameQuery = new TermQuery(new Term("name", folderName));
			
			// Checks whether a folder with the given 'name' exists in the index.
			BooleanQuery q = new BooleanQuery();
			q.add(new BooleanClause(typeQuery, Occur.MUST));
			q.add(new BooleanClause(nameQuery, Occur.MUST));
			
			IndexSearcher searcher = IndexManager.getSearcher();
			// TODO: Review this logic, as it should probably be possible to multiple folders to share the same name.
			TopDocs docs = searcher.search(q, 1);

			if (docs.totalHits > 0) {
				int docId = docs.scoreDocs[0].doc;
				rootFolder = fromLuceneDoc(searcher.doc(docId));
				
				IrisFolder folder = rootFolder;
				boolean finish = false;
				while (!finish) {
					Query idQuery = new TermQuery(new Term("parentId", folder.getId()));
					
					// Search for folder or e-mails inside the 'rootFolder'.
					q = new BooleanQuery();
					q.add(new BooleanClause(typeQuery, Occur.SHOULD));
					q.add(new BooleanClause(new TermQuery(new Term("type", "email")), Occur.SHOULD));
					q.add(new BooleanClause(idQuery, Occur.MUST));

					docs = searcher.search(q, searcher.getIndexReader().numDocs());
					
					finish = true;
					if (docs.totalHits > 0) {
						for (ScoreDoc sDoc : docs.scoreDocs) {
							Document doc = searcher.doc(sDoc.doc);
							String type = doc.getField("type").stringValue();
							if (type.equals("irisFolder")) {
								finish = false;
								IrisFolder childFolder = fromLuceneDoc(doc);
								folder.addElement(childFolder);
								folder = childFolder;
							} else if (type.equals("email")) {
								EmailMessage childEmail = EmailDAO.instance().fromLuceneDoc(doc);
								childEmail.setFolder(folder);
								folder.addElement(childEmail);
							} else {
								throw new DBException("Can't handle document type: '" + type + "'.", new Exception());
							}
						}
					}
				}
			}
		} catch (Exception e) {
			throw new DBException("Folder '" + folderName + "' not found.", e);
		}
		
		return rootFolder;
	}
	
	// IMPORTANT: This method will need to be updated to support multiple folders. Probably, there'll be a property 
	// `IrisFolder parent` in the `IrisFolder` class, so that a folder object knows its parent. 
	public void save(IrisFolder folder) throws DBException {
		try {			
			if (folder.getId() == null) { // Create
				create(null, folder);
				IndexManager.getWriter().commit();
			} else {
				Query typeQuery = new TermQuery(new Term("type", "irisFolder"));
				Query idQuery = new TermQuery(new Term("id", folder.getId()));
				
				// Checks whether a folder with the given 'id' exists in the index.
				BooleanQuery q = new BooleanQuery();
				q.add(new BooleanClause(typeQuery, Occur.MUST));
				q.add(new BooleanClause(idQuery, Occur.MUST));

				IndexSearcher searcher = IndexManager.getSearcher();
				TopDocs docs = searcher.search(q, 1);				

				if (docs.totalHits > 0) { // Case doc exists, updates it!
					update(folder);
				} else { // Otherwise, creates a new document.
					create(null, folder);
					// Commit is done here because a lot of adds are doing inside 'create'.
					// If something fails, a rollback is performed inside the 'catch' below.
					IndexManager.getWriter().commit();
				}
			}
		} catch (DBException e) {
			throw e;
		} catch (IOException e) { // IndexSearcher related failures.
			try {
				IndexManager.getWriter().rollback();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			throw new DBException("An error occured while saving folder.", e);
		}
	}
	
	// IMPORTANT: After calling this function, call `IndexManager.getWriter().commit()`.
	private void create(IrisFolder parent, FolderContent content) throws IOException {
		Document doc = null;
		
		//if (content.getId() == null)
		content.setId(UUID.randomUUID().toString());
		
		if (content instanceof EmailMessage) {
			doc = EmailDAO.instance().toLuceneDoc((EmailMessage) content);
		} else if (content instanceof IrisFolder) {
			IrisFolder folder = (IrisFolder) content;
			doc = toLuceneDoc(folder);
			for (FolderContent fc : folder.getContents())
				create(folder, fc);
		}
		
		if (parent != null) { // Root folders do not have a field 'parentId'.
			doc.add(new StringField("parentId", parent.getId(), Store.YES));
		}
		
		doc.add(new StringField("id", content.getId(), Store.YES));
		
		IndexManager.getWriter().addDocument(doc);
	}

	// TODO: Implement!
	private void update(IrisFolder folder) throws DBException {
		throw new DBException("Update is not implemented yet!", new Exception());
	}
	
	private Document toLuceneDoc(IrisFolder folder) {
		Document folderDoc = new Document();
		folderDoc.add(new StringField("type", "irisFolder", Store.YES));
		folderDoc.add(new StringField("name", folder.getName(), Store.YES));
		return folderDoc;
	}
	
	private IrisFolder fromLuceneDoc(Document doc) {
		IrisFolder folder = new IrisFolder();
		folder.setId(doc.getField("id").stringValue());
		folder.setName(doc.getField("name").stringValue());
		return folder;
	}

}
