package br.unb.cic.iris.persistence.lucene;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopFieldDocs;

import br.unb.cic.iris.core.exception.DBException;
import br.unb.cic.iris.core.model.EmailMessage;
import br.unb.cic.iris.core.model.Tag;
import br.unb.cic.iris.persistence.ITagDAO;

public class TagDAO extends LuceneDoc<Tag> implements ITagDAO {
	
	private static TagDAO instance;
	
	public TagDAO() {}
	
	public static TagDAO instance() {
		if (instance == null) {
			instance = new TagDAO();
		}
		return instance;
	}

	@Override
	public Tag findOrCreateByName(String name) throws DBException {
		Tag tag = new Tag(name);
		try {
			Query query = new TermQuery(new Term("tags", name));
			IndexSearcher searcher = IndexManager.getSearcher();
			// Take the top one if it exists.
			TopDocs docs = searcher.search(query, 1);
			for (int i = 0; i < docs.totalHits; i++) {
				Document doc = searcher.doc(docs.scoreDocs[i].doc);
				tag.getMessages().add(EmailDAO.instance().fromLuceneDoc(doc));
			}
		} catch (IOException e) {
			throw new DBException("An error occurred while retrieving tag", e);
		}
		return tag;
	}

	@Override
	public List<Tag> findTagsByEmailMessage(String messageId)
			throws DBException {
		try {
			if(messageId == null)
				throw new DBException("Tried to retrieve tags for message of ID null.", new Exception());
			
			Query typeQuery = new TermQuery(new Term("type", "email"));
			Query idQuery = new TermQuery(new Term("id", messageId));
			
			// Checks whether an address book entry with the given 'id' exists in the index.
			BooleanQuery q = new BooleanQuery();
			q.add(new BooleanClause(typeQuery, Occur.MUST));
			q.add(new BooleanClause(idQuery, Occur.MUST));

			IndexSearcher searcher = IndexManager.getSearcher();
			TopDocs docs = searcher.search(q, 1);				

			if (docs.totalHits > 0) {
				Document doc = searcher.doc(docs.scoreDocs[0].doc);
				String tagString = doc.get("tags");
				return tagsFromString(tagString);
			} else
				throw new DBException("The specified message does not exist.", new Exception());
		} catch (IOException e) {
			throw new DBException("Error occurred while trying to retrieve tags for message.", e);
		}
	}
	
	@Override
	public void addTagToMessage(String messageId, String tagName) throws DBException {
		try {
			if(messageId == null)
				throw new DBException("Tried to retrieve tags for message of ID null.", new Exception());
			
			Query typeQuery = new TermQuery(new Term("type", "email"));
			Query idQuery = new TermQuery(new Term("id", messageId));
			
			// Checks whether an address book entry with the given 'id' exists in the index.
			BooleanQuery q = new BooleanQuery();
			q.add(new BooleanClause(typeQuery, Occur.MUST));
			q.add(new BooleanClause(idQuery, Occur.MUST));

			IndexSearcher searcher = IndexManager.getSearcher();
			TopDocs docs = searcher.search(q, 1);				

			if (docs.totalHits > 0) {
				Tag newTag = new Tag(tagName);
				Document doc = searcher.doc(docs.scoreDocs[0].doc);
				String tagString = doc.get("tags");
				List<Tag> tags = tagsFromString(tagString);
				if (tags != null && tags.contains(newTag)) {
					return; //Message already has tag.
				} else {
					registerTag(tagName);
					if (tagString != null && !"".equals(tagString)) {
						tagString += "," + tagName;
					} else {
						tagString = tagName;
					}
					doc.add(new StringField("tags", tagString, Store.YES));
					doc.add(new StringField("id", messageId, Store.YES));
					IndexWriter writer = IndexManager.getWriter();
					writer.updateDocument(new Term("id", messageId), doc);
					writer.commit();
				}
			} else
				throw new DBException("The specified message does not exist.", new Exception());
		} catch (IOException e) {
			throw new DBException("Error occurred while trying to retrieve tags for message.", e);
		}
	}
	
	@Override
	public List<Tag> findAll() throws DBException {
		try {
			Query q = new TermQuery(new Term("type", "tags"));
			
			IndexSearcher searcher = IndexManager.getSearcher();
			TopDocs docs = searcher.search(q, 1);
			
			if (docs.totalHits > 0) {
				Document doc = searcher.doc(docs.scoreDocs[0].doc);
				String tagString = doc.get("registeredTags");
				return tagsFromString(tagString);
			} else {
				return new ArrayList<Tag>();
			}
		} catch (IOException e) {
			throw new DBException("Error occurred while trying to retrieve tags.", e);
		}
	}
	
	private void registerTag(String tagName) throws IOException {
		Query q = new TermQuery(new Term("type", "tags"));
		Tag newTag = new Tag(tagName);
		
		IndexSearcher searcher = IndexManager.getSearcher();
		TopDocs docs = searcher.search(q, 1);				

		if (docs.totalHits > 0) {
			Document doc = searcher.doc(docs.scoreDocs[0].doc);
			String tagString = doc.get("registeredTags");
			List<Tag> tags = tagsFromString(tagString);
			if (tags != null && tags.contains(newTag)) {
				return; //Message already has tag.
			} else {
				if (tagString != null && !"".equals(tagString)) {
					tagString += "," + tagName;
				} else {
					tagString = tagName;
				}
				doc.add(new StringField("registeredTags", tagString, Store.YES));
				doc.add(new StringField("type", "tags", Store.YES));
				IndexWriter writer = IndexManager.getWriter();
				writer.updateDocument(new Term("type", "tags"), doc);
				writer.commit();
			}
		} else {
			//Create tags document
			Document doc = new Document();
			doc.add(new StringField("type", "tags", Store.YES));
			doc.add(new StringField("registeredTags", tagName, Store.YES));
			IndexWriter writer = IndexManager.getWriter();
			writer.addDocument(doc);
			writer.commit();
		}
	}
	
	private List<Tag> tagsFromString(String tagString) {
		if (tagString == null || "".equals(tagString)) {
			return null;
		}
		String tags[] = tagString.split(",");
		List<Tag> tagList = new ArrayList<Tag>();
		for (String t : tags) {
			tagList.add(new Tag(t));
		}
		return tagList;
	}

	@Override
	protected Document toLuceneDoc(Tag t) {
		throw new RuntimeException("lucene.TagDAO.toLuceneDoc() shouldn't be called! No implementation should be provided.");
	}

	@Override
	protected Tag fromLuceneDoc(Document doc) {
		throw new RuntimeException("lucene.TagDAO.fromLuceneDoc() shouldn't be called! No implementation should be provided.");
	}

}
