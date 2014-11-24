package br.unb.cic.iris.core;

import java.util.List;

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
	
	private EmailMessage message1;
	private EmailMessage message2;
	private IrisFolder folder;
	
	private Tag tag1;
	
	private final static String TAG_NAME1 = "tag teste 1";
	
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
			
			message1 = new EmailMessage("email-to-test@test.com", "email-to-test@test.com", "email-to-test@test.com", "email-to-test@test.com", "test subject 1", "test message 1");
			message1.setFolder(folder);
			emailDao.saveMessage(message1);
			message2 = new EmailMessage("email-to-test@test.com", "test subject 2", "test message 2");
			message2.setFolder(folder);
			emailDao.saveMessage(message2);
		}
		catch(Exception e) {
			throw new Exception("could not setUp the tests", e);
		}
	}
	
	@Test
	public void addTagToMessage() throws Exception {
		try {
			
			tag1 = tagDao.findOrCreateByName(TAG_NAME1);
			tag1.setMessage(message1);
			tagDao.saveOrUpdate(tag1);
			
			List<Tag> tags = tagDao.findTagsByEmailMessage(message1);
			
			Assert.assertTrue("The retrieved set of tags for message1 does not contain added tag!", tags.contains(tag1));
		}
		catch(Exception e) {
			throw new Exception("Failed while testing! Exception occured.", e);
		}
	}
	
	@After
	public void tearDown() throws Exception {
		try {
			tagDao.delete(tag1);
			emailDao.delete(message1);
			emailDao.delete(message2);
			folderDao.delete(folder);
		}
		catch(Exception e) {
			throw new Exception("Could not tear down the tests.", e);
		}
	}

}
