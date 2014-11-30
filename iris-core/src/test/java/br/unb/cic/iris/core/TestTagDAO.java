package br.unb.cic.iris.core;

import java.util.List;
import java.util.Set;
import java.util.UUID;

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
	
	private final static String TAG_NAME1 = UUID.randomUUID().toString();
	private final static String TAG_NAME2 = UUID.randomUUID().toString();
	private final static String TEST_FOLDER_NAME = UUID.randomUUID().toString();
	
	@Before
	public void setUp() throws Exception {
		try {
			tagDao = TagDAO.instance();
			emailDao = EmailDAO.instance();
			folderDao = FolderDAO.instance();
			
			folder = folderDao.findByName(TEST_FOLDER_NAME);
			if (folder == null) {
				folder = new IrisFolder(TEST_FOLDER_NAME);
				folderDao.saveOrUpdate(folder);
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
			
			tagDao.addTagToMessage(message.getId(), TAG_NAME1);
			tag1 = tagDao.findOrCreateByName(TAG_NAME1);
			
			List<Tag> tags = tagDao.findTagsByEmailMessage(message.getId());
			
			Assert.assertTrue("The retrieved set of tags for message does not contain added tag1!", tags.contains(tag1));
			
			tagDao.addTagToMessage(message.getId(), TAG_NAME2);
			tag2 = tagDao.findOrCreateByName(TAG_NAME2);
			
			tags = tagDao.findTagsByEmailMessage(message.getId());
			
			Assert.assertTrue("The retrieved set of tags for message does not contain added tag1!", tags.contains(tag1));
			Assert.assertTrue("The retrieved set of tags for message does not contain added tag2!", tags.contains(tag2));
			
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
		
		List<Tag> tags = tagDao.findTagsByEmailMessage(message.getId());
		
		Assert.assertTrue("The retrieved set of tags for message does not contain added tag1!", tags.contains(tag1));
		
		tag1 = tagDao.findOrCreateByName(TAG_NAME1);
		tag1.getMessages().remove(message);
		tagDao.saveOrUpdate(tag1);
		
		tags = tagDao.findTagsByEmailMessage(message.getId());
		
		Assert.assertTrue("Failed to remove tag1 from message!", !tags.contains(tag1));
	}
	
	@Test
	public void deleteTag() throws Exception {
		tag1 = tagDao.findOrCreateByName(TAG_NAME1);
		tag1.getMessages().add(message);
		tagDao.saveOrUpdate(tag1);
		
		tagDao.delete(tag1);
		List<Tag> tags = tagDao.findTagsByEmailMessage(message.getId());
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
