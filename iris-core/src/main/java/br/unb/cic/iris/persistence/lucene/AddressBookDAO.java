package br.unb.cic.iris.persistence.lucene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.TopDocs;

import br.unb.cic.iris.core.exception.DBException;
import br.unb.cic.iris.core.model.AddressBookEntry;
import br.unb.cic.iris.persistence.IAddressBookDAO;

public class AddressBookDAO extends LuceneDoc<AddressBookEntry> implements IAddressBookDAO {

	private static AddressBookDAO instance;
	
	private AddressBookDAO() { } 
	
	public static AddressBookDAO instance() {
		if(instance == null) {
			instance = new AddressBookDAO();
		}
		return instance;
	}
	
	@Override
	public void save(AddressBookEntry entry) throws DBException {
		try {
			BooleanQuery q = new BooleanQuery();
			q.add(new BooleanClause(new TermQuery(new Term("type", "addressBook")), Occur.MUST));
			q.add(new BooleanClause(new TermQuery(new Term("id", entry.getId().toString())), Occur.MUST));
			
			IndexSearcher searcher = IndexManager.getSearcher();
			TopDocs docs = searcher.search(q, 1);
			
			IndexWriter writer = IndexManager.getWriter();
			if (docs.totalHits > 0) { // Update
				int docId = docs.scoreDocs[0].doc;
				String oldDocUUID = searcher.doc(docId).getField("uuid").stringValue();
				// The update operation actually removes the old document and adds a new one.
				writer.updateDocument(new Term("uuid", oldDocUUID), toLuceneDoc(entry));
			} else { // Create
				writer.addDocument(toLuceneDoc(entry));
			}
			writer.commit();
		} catch (IOException e) {
			throw new DBException("An error occured while saving address book entry.", e);
		}
	}

	@Override
	public AddressBookEntry find(String nick) throws DBException {
		AddressBookEntry entry = null;
		try {
			IndexSearcher searcher = IndexManager.getSearcher();
			TopDocs docs = searcher.search(new TermQuery(new Term("nick", nick)), 1);
			
			if (docs.totalHits > 0) {
				int docId = docs.scoreDocs[0].doc;
				entry = fromLuceneDoc(searcher.doc(docId));
			}
		} catch (IOException e) {
			throw new DBException("An error occured while finding address book entry by nick.", e);
		}
		
		return entry;
	}

	@Override
	public void delete(String nick) throws DBException {
		try {
			BooleanQuery q = new BooleanQuery();
			q.add(new BooleanClause(new TermQuery(new Term("type", "addressBook")), Occur.MUST));
			q.add(new BooleanClause(new TermQuery(new Term("nick", nick)), Occur.MUST));
			
			IndexWriter writer = IndexManager.getWriter();
			writer.deleteDocuments(q);
			writer.commit();
		} catch (IOException e) {
			throw new DBException("An error occured while deleting address book entry by nick.", e);
		}
	}

	@Override
	public Document toLuceneDoc(AddressBookEntry m) {
		List<Field> fields = new ArrayList<Field>();
		fields.add(new StringField("uuid", UUID.randomUUID().toString(), Store.YES));
		fields.add(new StringField("type", "addressBook", Store.NO));
		fields.add(new LongField("id", m.getId().longValue(), Store.YES));
		fields.add(new StringField("nick", m.getNick(), Store.YES));
		fields.add(new StringField("address", m.getAddress(), Store.YES));
		
		Document doc = new Document();
		for (Field f : fields)
			doc.add(f);
		
		return doc;
	}

	@Override
	protected AddressBookEntry fromLuceneDoc(Document doc) {
		AddressBookEntry entry = new AddressBookEntry();
		entry.setId(doc.getField("id").numericValue().longValue());
		entry.setNick(doc.getField("nick").stringValue());
		entry.setAddress(doc.getField("address").stringValue());
		return entry;
	}

}
