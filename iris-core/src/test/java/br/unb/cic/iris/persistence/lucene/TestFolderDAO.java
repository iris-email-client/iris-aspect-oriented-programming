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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.unb.cic.iris.core.exception.DBException;
import br.unb.cic.iris.core.model.EmailMessage;
import br.unb.cic.iris.core.model.FolderContent;
import br.unb.cic.iris.core.model.IrisFolder;

public class TestFolderDAO extends TestLucene {
	
	private static FolderDAO folderDAO = FolderDAO.instance();
	
	private IrisFolder rootFolder;
	
	private EmailMessage childEmail1, childEmail2;
	
	static {
		FS_IDX = true;
	}
	
	@Before
	public void setUp() throws IOException {		
		// Creates two e-mail messages.
		childEmail1 = new EmailMessage();
		childEmail1.setFrom("alexandrelucchesi@gmail.com");
		childEmail1.setTo("rbonifacio123@gmail.com");
		childEmail1.setCc("jeremiasmg@gmail.com");
		childEmail1.setBcc("somebcc@gmail.com");
		childEmail1.setSubject("Some subject");
		childEmail1.setMessage("Testing Lucene. :-)");
		Date date = null;
		try {
			date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2014-11-21 00:00:00");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		childEmail1.setDate(date);
		childEmail1.setFolder(new IrisFolder(19, "UnB"));
		
		childEmail2 = new EmailMessage();
		childEmail2.setFrom("jeremiasmg@gmail.com");
		childEmail2.setTo("alexandrelucchesi@gmail.com");
		childEmail2.setCc("");
		childEmail2.setBcc("");
		childEmail2.setSubject("Deal with it");
		childEmail2.setMessage("Just deal with it!");
		date = null;
		try {
			date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2014-12-25 06:30:21");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		childEmail2.setDate(date);
		childEmail2.setFolder(new IrisFolder(19, "UnB"));
		
		// Creates root folder and the two messages.
		rootFolder = new IrisFolder();
		rootFolder.setId(1);
		rootFolder.setName("A");
		rootFolder.addElement(childEmail1);
		rootFolder.addElement(childEmail2);
	}
	
	@Test
	public void testSave() throws IOException, DBException {
		folderDAO.save(rootFolder);
	}
	
	@Test
	public void testFindByName() throws DBException {
		folderDAO.save(rootFolder);
		
		IrisFolder folder = folderDAO.findByName(rootFolder.getName());
		assertNotNull(folder);
		assertEquals(folder.getName(), rootFolder.getName());
		
		List<FolderContent> contents = folder.getContents();
		assertEquals(contents.size(), rootFolder.getContents().size());
		
		List<Integer> contentIDs = new ArrayList<Integer>();
		for (FolderContent fc : contents) {
			contentIDs.add(fc.getId());
		}
		
		assertTrue(contentIDs.contains(childEmail1.getId()));
		assertTrue(contentIDs.contains(childEmail2.getId()));
	}
	
//	@Override
//	@After
//	public void tearDownIndex() throws IOException {
//		IndexManager.closeIndex();
//		//clearFSDirectory();
//	}
	
}
