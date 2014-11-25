package br.unb.cic.iris.persistence.lucene;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import br.unb.cic.iris.core.exception.DBException;
import br.unb.cic.iris.core.model.EmailMessage;
import br.unb.cic.iris.core.model.IrisFolder;
import br.unb.cic.iris.persistence.IEmailDAO;

import br.unb.cic.iris.persistence.lucene.EmailDAO;

public class TestEmailDAO extends TestLucene {

	//private IEmailDAO emailDAO;
	private IEmailDAO emailDAO = EmailDAO.instance();
	
	private EmailMessage msg1, msg2;
	
//	static {
//		FS_IDX = true;
//		DEFAULT_IDX_DIR = LuceneDAOFactory.INDEX_DIR;
//	}
	
//	@Override
//	@Before
//	public void setUpIndex() throws IOException {
//		// Empty override, so that the class `IndexManager` is set statically by 
//		// `LuceneDAOFactory` (loaded on `setUp()` method).
//	};
	
	@Before
	public void setUp() throws IOException {
		//emailDAO = LuceneDAOFactory.instance().createEmailDAO();
		
		msg1 = new EmailMessage();
		msg1.setFrom("alexandrelucchesi@gmail.com");
		msg1.setTo("rbonifacio123@gmail.com");
		msg1.setCc("jeremiasmg@gmail.com");
		msg1.setBcc("somebcc@gmail.com");
		msg1.setSubject("Some subject");
		msg1.setMessage("Testing Lucene. :-)");
		Date date = null;
		try {
			date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2014-11-21 00:00:00");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		msg1.setDate(date);
		msg1.setFolder(new IrisFolder("19", "UnB"));
		
		msg2 = new EmailMessage();
		msg2.setFrom("jeremiasmg@gmail.com");
		msg2.setTo("alexandrelucchesi@gmail.com");
		msg2.setCc("");
		msg2.setBcc("");
		msg2.setSubject("Deal with it");
		msg2.setMessage("Just deal with it!");
		date = null;
		try {
			date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2014-12-25 06:30:21");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		msg2.setDate(date);
		msg2.setFolder(new IrisFolder("19", "UnB"));
	}
	
	@Test
	public void testCreate() throws DBException {
		emailDAO.saveMessage(msg1);
		emailDAO.saveMessage(msg2);
	}
	
	@Test
	public void testUpdate() throws DBException {
		// Creates an entry in the index.
		msg1.setId(null);
		emailDAO.saveMessage(msg1);
		
		// Entry was successfully created an now has an id.
		String previousId = msg1.getId();
		assertNotNull(previousId);
		
		// Updates entry.
		String from = msg1.getFrom();
		msg1.setFrom(msg1.getTo());
		msg1.setTo(from);
		emailDAO.saveMessage(msg1);
		
		// The id is kept after update.
		assertEquals(previousId, msg1.getId());
	}
	
	
	@Test
	public void testUpdateWithInvalidId() throws DBException, IOException {
		try {
			msg1.setId("19");
			emailDAO.saveMessage(msg1);
		} catch (DBException e) {
			return;
		}
		
		fail("Message should not be saved with an 'id' that does not exist in the index.");
	}
	
	@Test
	public void testLastMessage() throws DBException, IOException {
		assertNotEquals(msg1.getDate(), msg2.getDate());
		
		emailDAO.saveMessage(msg1);
		Date lastDate = emailDAO.lastMessageReceived();
		assertEquals(msg1.getDate(), lastDate);
		
		emailDAO.saveMessage(msg2);
		lastDate = emailDAO.lastMessageReceived();
		assertNotEquals(msg1.getDate(), lastDate);
		assertEquals(msg2.getDate(), lastDate);
	}
	
}
