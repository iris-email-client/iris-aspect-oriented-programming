package br.unb.cic.iris.persistence.lucene;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import br.unb.cic.iris.core.exception.DBException;
import br.unb.cic.iris.core.model.EmailMessage;
import br.unb.cic.iris.core.model.FolderContent;
import br.unb.cic.iris.core.model.IrisFolder;

public class TestFolderDAO extends TestLucene {
	
	private static FolderDAO folderDAO = FolderDAO.instance();
	
	private IrisFolder folder1, folder2;
	
	private EmailMessage email1, email2, email3;
	
//	static {
//		FS_IDX = true;
//	}
	
	@Before
	public void setUp() throws IOException {
		// Creates three mock e-mail messages.
		email1 = new EmailMessage();
		email1.setFrom("alexandrelucchesi@gmail.com");
		email1.setTo("rbonifacio123@gmail.com");
		email1.setCc("jeremiasmg@gmail.com");
		email1.setBcc("somebcc@gmail.com");
		email1.setSubject("Some subject");
		email1.setMessage("Testing Lucene. :-)");
		Date date = null;
		try {
			date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2014-11-21 00:00:00");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		email1.setDate(date);
		
		email2 = new EmailMessage();
		email2.setFrom("jeremiasmg@gmail.com");
		email2.setTo("alexandrelucchesi@gmail.com");
		email2.setCc("");
		email2.setBcc("");
		email2.setSubject("Deal with it");
		email2.setMessage("Just deal with it!");
		date = null;
		try {
			date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2014-12-25 06:30:21");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		email2.setDate(date);

		
		email3 = new EmailMessage();
		email3.setFrom("danielsandoval@gmail.com");
		email3.setTo("pedrosalum@gmail.com");
		email3.setCc("");
		email3.setBcc("");
		email3.setSubject("Loop Key");
		email3.setMessage("Not only a door 'openner'...");
		date = null;
		try {
			date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2014-11-24 06:05:00");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		email3.setDate(date);
		
		// Creates the inbox and outbox folders.
		folder1 = new IrisFolder();
		folder1.setName("FOLDER1");
		folder1.addElement(email1);
		folder1.addElement(email2);
		
		folder2 = new IrisFolder();
		folder2.setName("FOLDER2");
		folder2.addElement(email3);
	}
		
	@Test
	public void testSave() throws IOException, DBException {
		folderDAO.save(folder1);
		folderDAO.save(folder2);
		assertNotNull(folder1.getId());
		assertNotNull(folder2.getId());
	}
	
	@Test
	public void testFindByName() throws DBException {
		folderDAO.save(folder1);
		
		IrisFolder folder = folderDAO.findByName(folder1.getName());
		assertNotNull(folder);
		assertEquals(folder.getName(), folder1.getName());
		assertEquals(folder.getId(), folder1.getId());
		
		List<FolderContent> contents = folder.getContents();
		assertEquals(contents.size(), folder1.getContents().size());
		
		List<String> contentIDs = new ArrayList<String>();
		for (FolderContent fc : contents) {
			contentIDs.add(fc.getId());
		}
		
		assertTrue(contentIDs.contains(email1.getId()));
		assertTrue(contentIDs.contains(email2.getId()));
	}
	
	@Test
	public void testStandardFolders() throws DBException {
		folderDAO.findByName("INBOX");
		folderDAO.findByName("OUTBOX");
	}
	
//	@Override
//	@After
//	public void tearDownIndex() throws IOException {
//		IndexManager.closeIndex();
//		//clearFSDirectory();
//	}
	
}
