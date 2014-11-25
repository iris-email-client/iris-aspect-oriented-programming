package br.unb.cic.iris.persistence.lucene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;

import br.unb.cic.iris.core.exception.DBException;
import br.unb.cic.iris.core.model.AddressBookEntry;
import br.unb.cic.iris.persistence.IAddressBookDAO;

public class AddressBookDAO extends LuceneDoc<AddressBookEntry> implements
		IAddressBookDAO {

	private static AddressBookDAO instance;

	private AddressBookDAO() {
	}

	public static AddressBookDAO instance() {
		if (instance == null) {
			instance = new AddressBookDAO();
		}
		return instance;
	}

	@Override
	public void save(AddressBookEntry entry) throws DBException {
		try {			
			if (entry.getId() == null) { // Create
				create(entry);
			} else { // Update (maybe)
				Query typeQuery = new TermQuery(new Term("type", "addressBook"));
				Query idQuery = new TermQuery(new Term("id", entry.getId()));
				
				// Checks whether an address book entry with the given 'id' exists in the index.
				BooleanQuery q = new BooleanQuery();
				q.add(new BooleanClause(typeQuery, Occur.MUST));
				q.add(new BooleanClause(idQuery, Occur.MUST));

				IndexSearcher searcher = IndexManager.getSearcher();
				TopDocs docs = searcher.search(q, 1);				

				if (docs.totalHits > 0) // Case doc exists, updates it!
					update(entry);
				else // Otherwise, throws an error!
					throw new DBException("The specified entry does not exist.", new Exception());
			}
		} catch (DBException e) {
			throw e;
		} catch (IOException e) { // IndexSearcher related failures.
			throw new DBException("An error occured while saving address book entry.", e);
		}
	}

	private void create(AddressBookEntry entry) throws DBException {
		try {
			Document newDoc = toLuceneDoc(entry);
			String id = UUID.randomUUID().toString();
			newDoc.add(new StringField("id", id, Store.YES));
			
			IndexWriter writer = IndexManager.getWriter();
			writer.addDocument(newDoc);
			writer.commit();
			
			// Sets the new entry id.
			entry.setId(id);
		} catch (IOException e) {
			throw new DBException("An error occured while creating address book entry.", e);
		}
	}
	
	private void update(AddressBookEntry entry) throws DBException {
		try {
			// As it's an update operation, keep the previous id.
			// The update operation actually removes the old document and adds a new one.
			Document doc = toLuceneDoc(entry);
			doc.add(new StringField("id", entry.getId(), Store.YES));
			
			IndexWriter writer = IndexManager.getWriter();
			// As 'id' is actually an UUID, simply passing the 'id' is enough to update the right
			// document. It's not necessary to filter by type.
			writer.updateDocument(new Term("id", entry.getId()), doc);
			writer.commit();
		} catch (IOException e) {
			throw new DBException("An error occured while updating address book entry.", e);
		}
	}

	@Override
	public AddressBookEntry find(String nick) throws DBException {
		AddressBookEntry entry = null;
		try {
			BooleanQuery q = new BooleanQuery();
			q.add(new BooleanClause(new TermQuery(new Term("type", "addressBook")), Occur.MUST));
			q.add(new BooleanClause(new TermQuery(new Term("nick", nick)), Occur.MUST));
			
			IndexSearcher searcher = IndexManager.getSearcher();
			TopDocs docs = searcher.search(q, 1);

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
		fields.add(new StringField("type", "addressBook", Store.YES));
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
		entry.setId(doc.getField("id").stringValue());
		entry.setNick(doc.getField("nick").stringValue());
		entry.setAddress(doc.getField("address").stringValue());
		return entry;
	}

	public List<AddressBookEntry> findAll() throws DBException {
		throw new RuntimeException("not implemented yet");
	}

}
