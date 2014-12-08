package br.unb.cic.iris.persistence.lucene;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import br.unb.cic.iris.addressbook.model.AddressBookEntry;
import br.unb.cic.iris.addressbook.persistence.IAddressBookDAO;
import br.unb.cic.iris.addressbook.persistence.lucene.AddressBookDAO;
import br.unb.cic.iris.core.exception.DBException;

public class TestAddressBookDAO extends TestLucene {

	private static IAddressBookDAO addressBookDAO = AddressBookDAO.instance();
	
	private AddressBookEntry entry;
	
	@Before
	public void setUp() throws IOException {
		entry = new AddressBookEntry();
		entry.setNick("Alexandre Lucchesi");
		entry.setAddress("alexandrelucchesi@gmail.com");
		entry.setId("19");
	}
	
	@Test
	public void testCreate() throws DBException {
		try {
			entry.setId(null);
			addressBookDAO.save(entry);
		} catch (DBException e) {}
		
		AddressBookEntry entry2 = addressBookDAO.find("Alexandre Lucchesi");
		
		assertNotNull("Object should be in the index.", entry2);
		assertEquals(entry.getId(), entry2.getId());
		assertEquals(entry.getNick(), entry2.getNick());
		assertEquals(entry.getAddress(), entry2.getAddress());
	}
	
	
	@Test
	public void testFind() throws DBException, IOException {
		testCreate();
		AddressBookEntry entry = addressBookDAO.find("Alexandre Lucchesi");
		assertNotNull("Entry should exist.", entry);
	}

	
	@Test
	public void testUpdate() throws DBException {
		// Creates an entry in the index.
		entry.setId(null);
		addressBookDAO.save(entry);
		
		// Entry was successfully created an now has an id.
		String previousId = entry.getId();
		assertNotNull(previousId);
		
		// Updates entry.
		entry.setNick("Rodrigo Bonifácio");
		entry.setAddress("rbonifacio123@gmail.com");
		addressBookDAO.save(entry);
		
		// The id is kept after update.
		assertEquals(previousId, entry.getId());
		
		// The previous entry was deleted.
		assertNull(addressBookDAO.find("Alexandre Lucchesi"));
		
		// The new entry is found.
		assertNotNull(addressBookDAO.find("Rodrigo Bonifácio"));
	}
	
	
	@Test
	public void testUpdateWithInvalidId() throws DBException, IOException {
		try {
			addressBookDAO.save(entry);
		} catch (DBException e) {
			AddressBookEntry entry2 = addressBookDAO.find("Alexandre Lucchesi");
			assertNull("Object should not be in the index.", entry2);			
			return;
		}
		
		fail("Entry should not be saved with an 'id' that does not exist in the index.");
	}

	
}
