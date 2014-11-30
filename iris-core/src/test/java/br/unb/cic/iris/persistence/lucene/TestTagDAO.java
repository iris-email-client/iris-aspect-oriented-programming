package br.unb.cic.iris.persistence.lucene;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.unb.cic.iris.core.exception.DBException;
import br.unb.cic.iris.core.model.EmailMessage;
import br.unb.cic.iris.core.model.IrisFolder;
import br.unb.cic.iris.core.model.Tag;
import br.unb.cic.iris.persistence.IEmailDAO;

public class TestTagDAO extends TestLucene {

	private EmailMessage msg1;
	
	private IEmailDAO emailDAO = EmailDAO.instance();
	
	private TagDAO tagDAO = TagDAO.instance();

	private static String TAG_NAME = "test-tag1";
	
	@Before
	public void setUp() throws DBException {
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
		emailDAO.saveMessage(msg1);
	}
	
	@Test
	public void testAddTagToMessage() throws DBException {
		tagDAO.addTagToMessage(msg1.getId(), TAG_NAME);
		Tag tag = tagDAO.findOrCreateByName(TAG_NAME);
		List<Tag> tags = tagDAO.findTagsByEmailMessage(msg1);
		
		Assert.assertTrue(tag.getMessages().contains(msg1));
		Assert.assertTrue(tags.contains(tag));
	}
	
}
