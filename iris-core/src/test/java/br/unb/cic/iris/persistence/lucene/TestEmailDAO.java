package br.unb.cic.iris.persistence.lucene;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

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

public class TestEmailDAO extends TestLucene {

	private static IEmailDAO emailDAO = EmailDAO.instance();
	
	private EmailMessage msg1, msg2;
	
	@Before
	public void setUp() throws IOException {
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
		msg1.setFolder(new IrisFolder(19, "UnB"));
		
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
		msg2.setFolder(new IrisFolder(19, "UnB"));
	}
	
	@Test
	public void testSaveMessage() throws DBException {
		emailDAO.saveMessage(msg1);
		emailDAO.saveMessage(msg2);
	}
	
	@Test
	public void testSaveWithId() throws DBException, IOException {
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
