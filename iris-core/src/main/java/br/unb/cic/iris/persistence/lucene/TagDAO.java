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
	public List<Tag> findTagsByEmailMessage(EmailMessage message)
			throws DBException {
		try {
			if(message.getId() == null)
				throw new DBException("Tried to retrieve tags for message of ID null.", new Exception());
			
			Query typeQuery = new TermQuery(new Term("type", "email"));
			Query idQuery = new TermQuery(new Term("id", message.getId()));
			
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
	public void saveOrUpdate(Tag tag) throws DBException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(Tag tag) throws DBException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected Document toLuceneDoc(Tag t) {
		List<Field> fields = new ArrayList<Field>();
		fields.add(new StringField("type", "tag", Store.YES));
		fields.add(new StringField("name", t.getName(), Store.YES));
		if (t.getMessages().size() == 1)
			fields.add(new StringField("messageId", t.getMessages().iterator().next().getId(), Store.YES));
		
		Document doc = new Document();
		for (Field f : fields)
			doc.add(f);
		
		return doc;
	}

	@Override
	protected Tag fromLuceneDoc(Document doc) {
		Tag tag = new Tag();
		tag.setId(doc.getField("id").stringValue());
		tag.setName(doc.getField("name").stringValue());
		EmailMessage email = new EmailMessage();
		email.setId(doc.getField("messageId").stringValue());
		tag.getMessages().add(email);
		
		return tag;
	}

}