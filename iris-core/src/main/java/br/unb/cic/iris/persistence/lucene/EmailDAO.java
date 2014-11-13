package br.unb.cic.iris.persistence.lucene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.LongField;
import org.apache.lucene.index.IndexWriter;

import br.unb.cic.iris.core.exception.DBException;
import br.unb.cic.iris.core.model.EmailMessage;
import br.unb.cic.iris.persistence.IEmailDAO;

public class EmailDAO implements IEmailDAO {
	
	@Override
	public void saveMessage(EmailMessage message) throws DBException {
		IndexWriter writer;
		try {
			writer = IndexManager.getWriter();
			writer.addDocument(toLuceneDoc(message));
			writer.commit();
		} catch (IOException e) {
			throw new DBException("An error occurred while saving e-mail message.", e);
		}
	}

	@Override
	public Date lastMessageReceived() throws DBException {
		// TODO Auto-generated method stub
		return null;
	}
	
	private Document toLuceneDoc(EmailMessage m) {
		List<Field> fields = new ArrayList<Field>();
		fields.add(new Field("from", m.getFrom(), IndexManager.TYPE_STORED));
		fields.add(new Field("to", m.getTo(), IndexManager.TYPE_STORED));
		fields.add(new Field("cc", m.getCc(), IndexManager.TYPE_STORED));
		fields.add(new Field("bcc", m.getBcc(), IndexManager.TYPE_STORED));
		fields.add(new Field("subject", m.getSubject(), IndexManager.TYPE_STORED));
		fields.add(new Field("message", m.getMessage(), IndexManager.TYPE_STORED));
		fields.add(new Field("date", m.getDate().toString(), IndexManager.TYPE_STORED));
		fields.add(new LongField("folderId", m.getFolder().getId().longValue(), Store.YES));
		
		Document doc = new Document();
		for (Field f : fields)
			doc.add(f);
		
		return doc;
	}


}
