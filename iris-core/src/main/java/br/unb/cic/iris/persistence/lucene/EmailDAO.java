package br.unb.cic.iris.persistence.lucene;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.SortField.Type;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopFieldDocs;

import br.unb.cic.iris.core.exception.DBException;
import br.unb.cic.iris.core.model.EmailMessage;
import br.unb.cic.iris.persistence.IEmailDAO;

public class EmailDAO extends LuceneDoc<EmailMessage> implements IEmailDAO {
	
	private static EmailDAO instance;
	
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
			IndexWriter writer = IndexManager.getWriter();
			writer.addDocument(toLuceneDoc(message));
			writer.commit();
		} catch (IOException e) {
			throw new DBException("An error occured while saving e-mail message.", e);
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
		fields.add(new StringField("uuid", UUID.randomUUID().toString(), Store.YES));
		fields.add(new StringField("type", "email", Store.YES));
		fields.add(new StringField("from", m.getFrom(), Store.YES));
		fields.add(new StringField("to", m.getTo(), Store.YES));
		fields.add(new StringField("cc", m.getCc(), Store.YES));
		fields.add(new StringField("bcc", m.getBcc(), Store.YES));
		fields.add(new TextField("subject", m.getSubject(), Store.YES));
		fields.add(new TextField("message", m.getMessage(), Store.YES));
		fields.add(new StringField("date", DateTools.dateToString(m.getDate(),
				DateTools.Resolution.SECOND), Store.YES));
		fields.add(new LongField("folderId", m.getFolder().getId().longValue(), Store.YES));
		
		Document doc = new Document();
		for (Field f : fields)
			doc.add(f);
		
		return doc;
	}

	@Override
	protected EmailMessage fromLuceneDoc(Document doc) {
		EmailMessage email = new EmailMessage();
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

}