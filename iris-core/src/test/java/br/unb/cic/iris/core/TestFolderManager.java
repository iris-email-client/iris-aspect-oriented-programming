package br.unb.cic.iris.core;

import java.util.UUID;
import java.util.ArrayList;
import java.util.List;

import br.unb.cic.iris.core.model.IrisFolder;
import br.unb.cic.iris.core.model.EmailMessage;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestFolderManager {
	
	private final static String FOLDER_NAME1 = UUID.randomUUID().toString();
	private final static String FOLDER_NAME2 = UUID.randomUUID().toString();
	private final static String SUBJECT = UUID.randomUUID().toString();
	
	@Before
	public void setup() throws Exception{
		//cria folder 1
		//cria folder 2
		// adiciona msg no folder 1
		// adiciona msg no folder 2
		
		try {
			
		} catch (Exception e) {
			throw new Exception("Faild while setting up Folder Test.", e);
		}
		
	}
	
	@After
	public void tearDown() throws Exception{
		// deleta msg1
		// deleta msg2
		// deleta folder 1
		// deleta folder 2
		
		try {
			
		} catch (Exception e) {
			throw new Exception("Faild while tearing down Folder Test.", e);
		}
		
	}
	
	@Test
	public void testChangeToFolder() throws Exception {
		
		try {
			
			String oldFolderName = FolderManager.instance().getCurrentFolderName();
			IrisFolder folder = FolderManager.instance().changeToFolder(FOLDER_NAME1);
			String newFolderName = FolderManager.instance().getCurrentFolderName();
			Boolean sameFolder = (oldFolderName.equals(newFolderName));
			
			Assert.assertTrue("Folder is not consistent between two calls", !folder.getName().equals(newFolderName));
			Assert.assertTrue("Changing from folder A to B faild", sameFolder);
			Assert.assertTrue("Changed to an unexpected folder", !newFolderName.equals(FOLDER_NAME1));
			
			
		} catch (Exception e) {
			throw new Exception("Faild while changing from folder A to folder B", e);
		}
		
		
	}
	
	@Test
	public void testListFolders() throws Exception{
		
		try{
			List<IrisFolder> folders = FolderManager.instance().listFolders();
			
			Boolean correctFolder = true;
			Integer counter = 0;
			for(IrisFolder folder : folders){
				if(!folder.getName().equals(FOLDER_NAME1) || !folder.getName().equals(FOLDER_NAME2))
					correctFolder = false;
				counter++;
					
			}
			
			Assert.assertTrue("Folders that are not suposed to exist were found", !correctFolder);
			Assert.assertTrue("One or more folders werent retrived as it should", counter<2);
			Assert.assertTrue("Duplicated folder retrieved", counter>2 && correctFolder);
	
		}
		catch(Exception e){
			throw new Exception("Faild while listing all folders", e);
		}
	}
	
	@Test
	public void testListFolderMessages() throws Exception {
		try{
			List<EmailMessage> messages = FolderManager.instance().listFolderMessages();
			
			Boolean correctMessage = true;
			Integer counter = 0;
			for(EmailMessage message : messages){
				if(!message.getSubject().equals(SUBJECT))
					correctMessage = false;
				counter++;
			}
			
			Assert.assertTrue("Folder cointais messages that shouldnt be there.", !correctMessage);
			Assert.assertTrue("One or more messages werent retrived as it should.", counter<2);
			Assert.assertTrue("Duplicated message retrieved", counter>2 && correctMessage);
		}
		catch(Exception e){
			throw new Exception("Faild while listing all messages from one folder", e);
		}
		
	}

}
