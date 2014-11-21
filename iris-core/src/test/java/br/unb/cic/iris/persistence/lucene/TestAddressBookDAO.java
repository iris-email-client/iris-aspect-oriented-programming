package br.unb.cic.iris.persistence.lucene;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.unb.cic.iris.core.exception.DBException;
import br.unb.cic.iris.core.model.AddressBookEntry;
import br.unb.cic.iris.persistence.IAddressBookDAO;

public class TestAddressBookDAO {
	
	// When set to 'true', creates the index in the filesystem in the path specified by 'DEFAULT_IDX_DIR'.
	// When 'false', creates the index in RAM.
	private static final boolean FS_IDX = true;
	
	// Don't forget to mkdir "~/.iris/lucene_idx" and update below.
	private static String DEFAULT_IDX_DIR = "/home/alexandrelucchesi/.iris/lucene_idx/";

	private IAddressBookDAO addressBookDAO;
	
	private AddressBookEntry entry;
	
	@Before
	public void setUp() throws IOException, DBException {
		if (FS_IDX)
			IndexManager.createIndex(DEFAULT_IDX_DIR);
		else
			IndexManager.createIndex(null);
		
		addressBookDAO = AddressBookDAO.instance();
		entry = new AddressBookEntry();
		entry.setNick("Alexandre Lucchesi");
		entry.setAddress("alexandrelucchesi@gmail.com");
		entry.setId(19L);
		
		addressBookDAO.save(entry);
		
		IndexManager.closeIndex();
	}
	
	@Test
	public void testDummy() {
	
	}
	
//	@Test
//	public void testSaveWithoutId() throws DBException {
//		addressBookDAO.save(entry);
//		AddressBookEntry entry2 = addressBookDAO.find("Alexandre Lucchesi");
//		assertEquals(entry, entry2);
//	}
	
//	@Test
//	public void testSaveWithId() throws DBException, IOException {
//		entry.setId(19L);
//		addressBookDAO.save(entry);
//		AddressBookEntry entry2 = addressBookDAO.find("Alexandre Lucchesi");
//		assertEquals(entry, entry2);
//	}
	
//	@After
//	public void tearDown() throws IOException {
//		IndexManager.closeIndex();
//	}
	
}


//public class TestLucene {
//
//	// Don't forget to mkdir "~/.iris/lucene_idx" and update below.
//	private static String DEFAULT_IDX_DIR = "/home/alexandrelucchesi/.iris/lucene_idx/";
//
//	public static void main(String[] args) throws DBException, IOException,
//			ParseException {
//		//setUp(DEFAULT_IDX_DIR, true);
//		search(DEFAULT_IDX_DIR);
//	}
//
//	// -------------------------------------------------------
//	// SETUP
//	// -------------------------------------------------------
//
//	static EmailMessage msg1, msg2;
//
//	
//	static {
//		msg1 = new EmailMessage();
//		msg1.setFrom("alexandrelucchesi@gmail.com");
//		msg1.setTo("rbonifacio123@gmail.com");
//		msg1.setCc("jeremiasmg@gmail.com");
//		msg1.setBcc("somebcc@gmail.com");
//		msg1.setSubject("Some subject");
//		msg1.setMessage("Testing Lucene. :-)");
//		msg1.setDate(new Date());
//		msg1.setFolder(new IrisFolder(19, "UnB"));
//
//		msg2 = new EmailMessage();
//		msg2.setFrom("jeremiasmg@gmail.com");
//		msg2.setTo("alexandrelucchesi@gmail.com");
//		msg2.setCc("");
//		msg2.setBcc("");
//		msg2.setSubject("Deal with it");
//		msg2.setMessage("I would NEVA put my name on it!");
//		Date date = null;
//		try {
//			date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2014-12-25 06:30:21");
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
//		msg2.setDate(date);
//		msg2.setFolder(new IrisFolder(19, "UnB"));
//	}
//
//	private static void setUp(String indexDir, boolean create)
//			throws DBException, IOException, ParseException {
//		if (create) {
//			IndexManager.createIndex(indexDir);
//		} else {
//			IndexManager.setIndex(indexDir); // Set index directory.
//		}
//
//		EmailDAO emailDAO = EmailDAO.instance();
//
//		emailDAO.saveMessage(msg1);
//		emailDAO.saveMessage(msg2);
//
//		IndexManager.closeIndex();
//		
//		System.out.println("Success! :-)");
//	}
//
//	// -------------------------------------------------------
//	// SEARCH
//	// -------------------------------------------------------
//	private static void search(String indexDir) throws DBException, IOException {
//		IndexManager.setIndex(indexDir); // Set index directory.
//
//		EmailDAO emailDAO = EmailDAO.instance();
//
//		Date lastDate = emailDAO.lastMessageReceived();
//		System.out.println(lastDate);
//
//		IndexManager.closeIndex();
//	}
//
//}
