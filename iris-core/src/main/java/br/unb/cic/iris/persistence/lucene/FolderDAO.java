package br.unb.cic.iris.persistence.lucene;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeQuery;
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
	public IrisFolder findById(int folderId) throws DBException {
		IrisFolder folder = null;
		try {
			Query typeQuery = new TermQuery(new Term("type", "irisFolder"));
			Query idQuery = NumericRangeQuery.newIntRange("id", folderId, folderId, true, true);

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
					Query idQuery = NumericRangeQuery.newIntRange("parentId", folder.getId(), folder.getId(), true, true);
					
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
	
	public void save(IrisFolder folder) throws DBException {
		try {			
			if (folder.getId() == null) { // Create
				throw new DBException("Can't create folder with no 'id'.", new Exception());
			} else {
				Query typeQuery = new TermQuery(new Term("type", "irisFolder"));
				Query idQuery = NumericRangeQuery.newIntRange("id", folder.getId().intValue(), folder.getId(), true, true);
				
				// Checks whether a folder with the given 'id' exists in the index.
				BooleanQuery q = new BooleanQuery();
				q.add(new BooleanClause(typeQuery, Occur.MUST));
				q.add(new BooleanClause(idQuery, Occur.MUST));

				IndexSearcher searcher = IndexManager.getSearcher();
				TopDocs docs = searcher.search(q, 1);				

				if (docs.totalHits > 0) { // Case doc already exists, updates it!
					int docId = docs.scoreDocs[0].doc;
					update(searcher.doc(docId), folder);
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
	
	private void create(IrisFolder parent, FolderContent content) throws IOException {
		Document doc = null;
		if (content instanceof EmailMessage) {
			doc = EmailDAO.instance().toLuceneDoc((EmailMessage) content);
		} else if (content instanceof IrisFolder) {
			IrisFolder folder = (IrisFolder) content;
			doc = toLuceneDoc(folder);
			for (FolderContent fc : folder.getContents()) {
				create(folder, fc);
			}
		}
		
		doc.add(new IntField("parentId", parent != null ? parent.getId().intValue() : -1, Store.YES));
		IndexManager.getWriter().addDocument(doc);
	}

	// TODO: Implement!
	private void update(Document oldDoc, IrisFolder entry) throws DBException {
		throw new DBException("Update is not implemented yet!", new Exception());
	}
	
	private Document toLuceneDoc(IrisFolder folder) {
		Document folderDoc = new Document();
		folderDoc.add(new StringField("type", "irisFolder", Store.YES));
		folderDoc.add(new IntField("id", folder.getId().intValue(), Store.YES));
		folderDoc.add(new StringField("name", folder.getName(), Store.YES));
		return folderDoc;
	}
	
	private IrisFolder fromLuceneDoc(Document doc) {
		IrisFolder folder = new IrisFolder();
		folder.setId(doc.getField("id").numericValue().intValue());
		folder.setName(doc.getField("name").stringValue());
		return folder;
	}
	
//	private void create(IrisFolder folder) throws DBException {
//		try {
//			IndexWriter writer = IndexManager.getWriter();
//
//			Node<Document> folderTree = toLuceneDoc(folder);
//			StringField rootUUIDField = new StringField("uuid", UUID.randomUUID().toString(), Store.YES);
//			folderTree.value.add(rootUUIDField);
//			
//			for (Node<Document> node : folderTree.children) {
//				String type = node.value.getField("type").stringValue();
//				if (type.equals("irisFolder")) {
//					
//				} else if (type.equals("irisFolder")) {
//					
//				} else {
//					throw new Exception("Can't handle type '" + type + "'.");
//				}
//			}
//			
//			
//			
//			Document newDoc = toLuceneDoc(folder);
//			newDoc.add(uuidField);
//			writer.addDocument(newDoc);
			
//			writer.commit();
//		} catch (IOException e) {
//			throw new DBException("An error occured while creating address book entry.", e);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//	
//	private void update(Document oldDoc, IrisFolder entry) throws DBException {
//		try {
//			IndexWriter writer = IndexManager.getWriter();
//
//			String oldDocUUID = oldDoc.getField("uuid").stringValue();
//			// As it's an update operation, keep the previous UUID.
//			StringField uuidField = new StringField("uuid", oldDocUUID, Store.YES);
//			// The update operation actually removes the old document and adds a new one.
//			Document newDoc = toLuceneDoc(entry);
//			newDoc.add(uuidField);
//			writer.updateDocument(new Term("uuid", oldDocUUID), newDoc);
//			writer.commit();
//		} catch (IOException e) {
//			throw new DBException("An error occured while updating address book entry.", e);
//		}
//	}
//	
//				
//	private class Node<T> {
//		private T value;
//		private List<Node<T>> children;
//	}
//	
//	private Node<Document> toLuceneDoc(IrisFolder folder) throws Exception {
//		Document folderDoc = new Document();
//		folderDoc.add(new StringField("type", "irisFolder", Store.NO));
//		folderDoc.add(new IntField("id", folder.getId().intValue(), Store.YES));
//		folderDoc.add(new StringField("name", folder.getName(), Store.YES));
//
//		Node<Document> folderTree = new Node<Document>();
//		folderTree.value = folderDoc;
//		folderTree.children = new ArrayList<Node<Document>>();
//
//		for (FolderContent fc : folder.getContents()) {
//			if (fc instanceof EmailMessage) {
//				Document emailDoc = EmailDAO.instance().toLuceneDoc((EmailMessage) fc);
//				Node<Document> emailLeaf = new Node<Document>();
//				emailLeaf.value = emailDoc;
//				emailLeaf.children = null;
//				folderTree.children.add(emailLeaf);
//			} else if (fc instanceof IrisFolder) {
//				Node<Document> childFolderTree = toLuceneDoc((IrisFolder) fc);
//				folderTree.children.add(childFolderTree);
//			} else {
//				throw new Exception("Can't handle type: '" + fc.getClass().getName() + "'.");
//			}
//		}
//		
//		return folderTree;
//	}

}
