package br.unb.cic.iris.persistence.lucene;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.SortField.Type;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopFieldDocs;

import br.unb.cic.iris.core.exception.DBException;
import br.unb.cic.iris.core.model.EmailMessage;
import br.unb.cic.iris.persistence.IEmailDAO;

public class EmailDAO extends LuceneDoc<EmailMessage> implements IEmailDAO {

	private static EmailDAO instance;

	public class EmailCollector extends AbstractCollector {

		private IndexSearcher searcher;

		private List<EmailMessage> result;

		public EmailCollector(IndexSearcher searcher, List<EmailMessage> result) {
			this.searcher = searcher;
			this.result = result;
		}

		public void tryCollect(int doc) throws Exception {
			Document d = searcher.doc(doc);
			result.add(fromLuceneDoc(d));
		}
	}

	public abstract class AbstractCollector extends Collector {

		private int base;

		public void setScorer(Scorer scorer) {
		}

		public boolean acceptsDocsOutOfOrder() {
			return true;
		}

		public void collect(int doc) {
			try {
				tryCollect(doc + base);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		abstract void tryCollect(int doc) throws Exception;

		public void setNextReader(AtomicReaderContext context) throws IOException {
			 this.base = context.docBase;
		}
	}
	private EmailDAO() { } 
	
	public static EmailDAO instance() {
		if(instance == null) {
			instance = new EmailDAO();
		}
		return instance;
	}
	
	@Override
	public void saveMessage(EmailMessage message) throws DBException {		
		try {			
			if (message.getId() == null) { // Create
				create(message);
			} else { // Update (maybe)
				Query typeQuery = new TermQuery(new Term("type", "email"));
				Query idQuery = new TermQuery(new Term("id", message.getId()));
				
				// Checks whether an address book entry with the given 'id' exists in the index.
				BooleanQuery q = new BooleanQuery();
				q.add(new BooleanClause(typeQuery, Occur.MUST));
				q.add(new BooleanClause(idQuery, Occur.MUST));

				IndexSearcher searcher = IndexManager.getSearcher();
				TopDocs docs = searcher.search(q, 1);				

				if (docs.totalHits > 0) // Case doc exists, updates it!
					update(message);
				else // Otherwise, throws an error!
					throw new DBException("The specified message does not exist.", new Exception());
			}
		} catch (DBException e) {
			throw e;
		} catch (IOException e) { // IndexSearcher related failures.
			throw new DBException("An error occured while saving message.", e);
		}
	}
	
	private void create(EmailMessage message) throws DBException {
		try {
			Document newDoc = toLuceneDoc(message);
			String id = UUID.randomUUID().toString();
			newDoc.add(new StringField("id", id, Store.YES));
			
			IndexWriter writer = IndexManager.getWriter();
			writer.addDocument(newDoc);
			writer.commit();
			
			// Sets the new entry id.
			message.setId(id);
		} catch (IOException e) {
			throw new DBException("An error occured while creating message.", e);
		}
	}
	
	private void update(EmailMessage message) throws DBException {
		try {
			// As it's an update operation, keep the previous id.
			// The update operation actually removes the old document and adds a new one.
			Document doc = toLuceneDoc(message);
			doc.add(new StringField("id", message.getId(), Store.YES));
			
			IndexWriter writer = IndexManager.getWriter();
			// As 'id' is actually an UUID, simply passing the 'id' is enough to update the right
			// document. It's not necessary to filter by type.
			writer.updateDocument(new Term("id", message.getId()), doc);
			writer.commit();
		} catch (IOException e) {
			throw new DBException("An error occured while updating message.", e);
		}
	}

	@Override
	public Date lastMessageReceived() throws DBException {
		Date date = null;
		try {
			// Retrieves only documents whose 'type' is 'email'.
			Query query = new TermQuery(new Term("type", "email"));
			// Sorts documents by date (in descending order).
			Sort sort = new Sort(new SortField("date", Type.STRING, true));
			
			IndexSearcher searcher = IndexManager.getSearcher();
			// Take the top one if it exists.
			TopFieldDocs docs = searcher.search(query, 1, sort);
			if (docs.totalHits > 0) {
				Document doc = searcher.doc(docs.scoreDocs[0].doc);
				date = DateTools.stringToDate(doc.get("date"));
			}
		} catch (IOException e) {
			throw new DBException("An error occurred while retrieving last message received", e);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return date;
	}
	
	@Override
	protected Document toLuceneDoc(EmailMessage m) {
		List<Field> fields = new ArrayList<Field>();
		fields.add(new StringField("type", "email", Store.YES));
		fields.add(new StringField("from", m.getFrom(), Store.YES));
		fields.add(new StringField("to", m.getTo(), Store.YES));
		fields.add(new StringField("cc", m.getCc(), Store.YES));
		fields.add(new StringField("bcc", m.getBcc(), Store.YES));
		fields.add(new TextField("subject", m.getSubject(), Store.YES));
		fields.add(new TextField("message", m.getMessage(), Store.YES));
		fields.add(new StringField("date", DateTools.dateToString(m.getDate() == null ? Calendar.getInstance().getTime() : m.getDate() ,
				DateTools.Resolution.SECOND), Store.YES));
		if (m.getFolder() != null)
			fields.add(new StringField("folderId", m.getFolder().getId(), Store.YES));
		
		Document doc = new Document();
		for (Field f : fields)
			doc.add(f);
		
		return doc;
	}

	@Override
	    //TODO: this must be private, fix it later.
	public EmailMessage fromLuceneDoc(Document doc) {
		EmailMessage email = new EmailMessage();
		email.setId(doc.getField("id").stringValue());
		email.setFrom(doc.getField("from").stringValue());
		email.setTo(doc.getField("to").stringValue());
		email.setCc(doc.getField("cc").stringValue());
		email.setBcc(doc.getField("bcc").stringValue());
		email.setSubject(doc.getField("subject").stringValue());
		email.setMessage(doc.getField("message").stringValue());
		try {
			email.setDate(DateTools.stringToDate(doc.getField("date").stringValue()));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//email.setFolder(doc.getField("folderId").stringValue());
		email.setFolder(null);
		
		return email;
	}
	
	@Override
	public List<EmailMessage> findByFolder(String folderId) throws DBException {
		List<EmailMessage> result = new ArrayList<EmailMessage>();
		try {
			Query typeQuery = new TermQuery(new Term("type", "email"));
			Query idQuery = new TermQuery(new Term("folderId", folderId));

			BooleanQuery q = new BooleanQuery();
			q.add(new BooleanClause(typeQuery, Occur.MUST));
			q.add(new BooleanClause(idQuery, Occur.MUST));

			IndexSearcher searcher = IndexManager.getSearcher();
			searcher.search(q, new EmailCollector(searcher, result));

		} catch (Exception e) {
			throw new DBException("Erro ocurred while retrieving messages from folder.", e);
		}
		return result;
	}

	@Override
	public EmailMessage findById(String uuid) throws DBException {
		EmailMessage message = null;
		try {
			Query typeQuery = new TermQuery(new Term("type", "email"));
			Query idQuery = new TermQuery(new Term("id", uuid));

			// Checks whether a folder with the given 'name' exists in the index.
			BooleanQuery q = new BooleanQuery();
			q.add(new BooleanClause(typeQuery, Occur.MUST));
			q.add(new BooleanClause(idQuery, Occur.MUST));

			IndexSearcher searcher = IndexManager.getSearcher();
			TopDocs docs = searcher.search(q, 1);

			if (docs.totalHits > 0) {
				int docId = docs.scoreDocs[0].doc;
				message = this.fromLuceneDoc(searcher.doc(docId));
			}

		} catch (Exception e) {
			throw new DBException("message '" + uuid + "' not found.", e);
		}
		return message;
	}

	@Override
	public void delete(EmailMessage message) throws DBException {
		throw new RuntimeException("method delete on lucene.EmailDAO has not been implemented yet");
	}

}
