package br.unb.cic.iris.core;

import java.util.UUID;
import java.util.ArrayList;
import java.util.List;

import br.unb.cic.iris.core.model.Tag;
import br.unb.cic.iris.core.model.EmailMessage;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class TestTagManager {
	
	private final static String TAG_NAME1 = UUID.randomUUID().toString();
	private final static String TAG_NAME2 = UUID.randomUUID().toString();
	private final static String TAG_NAME3 = UUID.randomUUID().toString();
	private final static String SUBJECT = UUID.randomUUID().toString();
	
	@Before
	public void setup() throws Exception{
		//Cirar mensagem 1
		//Criar mensagem 2
		TagManager.instance().saveTags(message1.getId(), TAG_NAME1);
		TagManager.instance().saveTags(message1.getId(), TAG_NAME2);
		TagManager.instance().saveTags(message2.getId(), TAG_NAME2);
		TagManager.instance().saveTags(message2.getId(), TAG_NAME3);
		
		try {
			
		} catch (Exception e) {
			throw new Exception("Faild while setting the test up!", e);
		}
		
	}
	
	@After
	public void tearDown() throws Exception{
		//Deletar Tags
		//Deletar msgs
		
		try {
			
		} catch (Exception e) {
			throw new Exception("Faild while tearing down the test!", e);
		}
		
	}
	
	@Test
	public void testFindAllTags() throws Exception{
		try {
			
			List<Tag> tags = TagManager.instance().findAll();
			
			Assert.assertTrue("One or more tags havent been found. ",!(tags.contains(TAG_NAME1) && tags.contains(TAG_NAME2) && tags.contains(TAG_NAME3)));

		} catch (Exception e) {
			throw new Exception("Faild while finding all tags", e);
		}
	}
	
	@Test
	public void testListMessagesByTag() throws Exception{
		try {
			
			List<EmailMessage> messages = TagManager.instance().listMessagesByTag(TAG_NAME2);
			
			Boolean correctMessage = true;
			for(EmailMessage LItem : messages){
				if(!LItem.getSubject().equals(SUBJECT)){
						correctMessage = false;
						break;
				}
			}
			
			
			Assert.assertTrue("One or more messages werent retrived",messages.size() != 2);
			Assert.assertTrue("Wrong message retrived", !correctMessage );
			
		} catch (Exception e) {
			throw new Exception("Faild while finding all tags", e);
		}
	}
	
}
