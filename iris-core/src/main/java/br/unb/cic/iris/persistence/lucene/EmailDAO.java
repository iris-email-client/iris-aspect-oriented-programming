package br.unb.cic.iris.persistence.lucene;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

public class EmailDAO implements IEmailDAO {
	
	//private static SimpleDateFormat dateFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Override
	public void saveMessage(EmailMessage message) throws DBException {
		try {
			IndexWriter writer = IndexManager.getWriter();
			writer.addDocument(toLuceneDoc(message));
			writer.commit();
		} catch (IOException e) {
			throw new DBException("An error occurred while saving e-mail message.", e);
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
	
	private Document toLuceneDoc(EmailMessage m) {
		List<Field> fields = new ArrayList<Field>();
		fields.add(new StringField("type", "email", Store.NO));
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

}
