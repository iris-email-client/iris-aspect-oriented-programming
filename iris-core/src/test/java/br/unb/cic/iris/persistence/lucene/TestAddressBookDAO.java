package br.unb.cic.iris.persistence.lucene;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import br.unb.cic.iris.core.exception.DBException;
import br.unb.cic.iris.core.model.AddressBookEntry;
import br.unb.cic.iris.persistence.IAddressBookDAO;

public class TestAddressBookDAO extends TestLucene {

	private static IAddressBookDAO addressBookDAO = AddressBookDAO.instance();
	
	private AddressBookEntry entry;
	
	@Before
	public void setUp() throws IOException {
		entry = new AddressBookEntry();
		entry.setNick("Alexandre Lucchesi");
		entry.setAddress("alexandrelucchesi@gmail.com");
		entry.setId(19L);
	}
	
	@Test
	public void testSaveWithoutId() throws DBException {
		try {
			entry.setId(null);
			addressBookDAO.save(entry);
			fail("Entry should not be saved without an 'id'.");
		} catch (DBException e) {}
		
		AddressBookEntry entry2 = addressBookDAO.find("Alexandre Lucchesi");
		assertNull("Object should not be in the index.", entry2);
	}
	
	@Test
	public void testSaveWithId() throws DBException, IOException {
		addressBookDAO.save(entry);
		AddressBookEntry entry2 = addressBookDAO.find("Alexandre Lucchesi");
		assertEquals(entry.getId(), entry2.getId());
		assertEquals(entry.getNick(), entry2.getNick());
		assertEquals(entry.getAddress(), entry2.getAddress());
	}
	
	@Test
	public void testUpdate() throws DBException {
		addressBookDAO.save(entry);
		AddressBookEntry orig = addressBookDAO.find("Alexandre Lucchesi");
		assertNotNull(orig);
		assertEquals(entry.getAddress(), orig.getAddress());
		
		entry.setNick("Rodrigo Bonifácio");
		entry.setAddress("rbonifacio123@gmail.com");
		addressBookDAO.save(entry);
		
		assertNull(addressBookDAO.find("Alexandre Lucchesi"));
		
		AddressBookEntry novel = addressBookDAO.find("Rodrigo Bonifácio");
		assertNotNull(novel);
		assertEquals(entry.getAddress(), novel.getAddress());
		
		assertEquals(orig.getId(), novel.getId());
	}
	
	@Test
	public void testFind() throws DBException, IOException {
		testSaveWithId();
		AddressBookEntry entry = addressBookDAO.find("Alexandre Lucchesi");
		assertNotNull("Entry should exist.", entry);
	}
	
}
