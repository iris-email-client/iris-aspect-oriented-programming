package br.unb.cic.iris.core;

import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.unb.cic.iris.core.model.EmailMessage;
import br.unb.cic.iris.core.model.IrisFolder;
import br.unb.cic.iris.core.model.Tag;
import br.unb.cic.iris.persistence.sqlite3.EmailDAO;
import br.unb.cic.iris.persistence.sqlite3.FolderDAO;
import br.unb.cic.iris.persistence.sqlite3.TagDAO;

public class TestTagDAO {
	
	private TagDAO tagDao;
	private EmailDAO emailDao;
	private FolderDAO folderDao;
	
	private EmailMessage message;
	private IrisFolder folder;
	
	private Tag tag1;
	private Tag tag2;
	
	private final static String TAG_NAME1 = "tag teste 1";
	private final static String TAG_NAME2 = "tag teste 2";
	
	@Before
	public void setUp() throws Exception {
		try {
			tagDao = TagDAO.instance();
			emailDao = EmailDAO.instance();
			folderDao = FolderDAO.instance();
			
			folder = folderDao.findByName("test-folder");
			if (folder == null) {
				folder = new IrisFolder("test-folder");
			}
			
			message = new EmailMessage("email-to-test@test.com", "email-to-test@test.com", "email-to-test@test.com", "email-to-test@test.com", "test subject 1", "test message 1");
			message.setFolder(folder);
			emailDao.saveMessage(message);

		}
		catch(Exception e) {
			throw new Exception("could not setUp the tests", e);
		}
	}
	
	@Test
	public void addTagToMessage() throws Exception {
		try {
			
			tag1 = tagDao.findOrCreateByName(TAG_NAME1);
			tag1.getMessages().add(message);
			tagDao.saveOrUpdate(tag1);
			
			List<Tag> tags = tagDao.findTagsByEmailMessage(message);
			
			Assert.assertTrue("The retrieved set of tags for message does not contain added tag1!", tags.contains(tag1));
			
			tag2 = tagDao.findOrCreateByName(TAG_NAME2);
			tag2.getMessages().add(message);
			tagDao.saveOrUpdate(tag2);
			
			tags = tagDao.findTagsByEmailMessage(message);
			
			Assert.assertTrue("The retrieved set of tags for message does not contain added tag1!", tags.contains(tag1));
			Assert.assertTrue("The retrieved set of tags for message does not contain added tag2!", tags.contains(tag2));
			for (Tag t : tags) {
				Assert.assertTrue("Tag " + t.getName() + " should have message1 but doens't!", t.getMessages().contains(message));
			}
			
		}
		catch(Exception e) {
			throw new Exception("Failed while testing! Exception occured.", e);
		}
	}
	
	@Test
	public void removeTagFromMessage() throws Exception {
		tag1 = tagDao.findOrCreateByName(TAG_NAME1);
		tag1.getMessages().add(message);
		tagDao.saveOrUpdate(tag1);
		
		List<Tag> tags = tagDao.findTagsByEmailMessage(message);
		
		Assert.assertTrue("The retrieved set of tags for message does not contain added tag1!", tags.contains(tag1));
		
		tag1 = tagDao.findOrCreateByName(TAG_NAME1);
		Set<EmailMessage> msgs = tag1.getMessages();
		tag1.getMessages().remove(message);
		tagDao.saveOrUpdate(tag1);
		
		tags = tagDao.findTagsByEmailMessage(message);
		
		Assert.assertTrue("Failed to remove tag1 from message!", !tags.contains(tag1));
	}
	
	@Test
	public void deleteTag() throws Exception {
		tag1 = tagDao.findOrCreateByName(TAG_NAME1);
		tag1.getMessages().add(message);
		tagDao.saveOrUpdate(tag1);
		
		tagDao.delete(tag1);
		List<Tag> tags = tagDao.findTagsByEmailMessage(message);
		Assert.assertTrue("Failed to delete tag1!", !tags.contains(tag1));
		
		tag1 = null;
	}
	
	@After
	public void tearDown() throws Exception {
		try {
			if (tag1 != null)
				tagDao.delete(tag1);
			if (tag2 != null)
				tagDao.delete(tag2);
			emailDao.delete(message);
			folderDao.delete(folder);
		}
		catch(Exception e) {
			throw new Exception("Could not tear down the tests.", e);
		}
	}

}
