/*
 * TestSystemFacade.java
 * ---------------------------------
 *  version: 0.0.1
 *  date: Sep 18, 2014
 *  author: rbonifacio
 *  list of changes: (none) 
 */
package br.unb.cic.iris.core;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;
import java.util.ArrayList;
import java.util.List;

import br.unb.cic.iris.core.model.EmailMessage;
import br.unb.cic.iris.core.model.AddressBookEntry;
import br.unb.cic.iris.core.model.IrisFolder;
import br.unb.cic.iris.mail.EmailProvider;


/**
 * @author ExceptionHandling
 *
 */
public class TestSystemFacade {

	private EmailProvider provider;
	private final static String SUBJECT = UUID.randomUUID().toString();
	private final static String TESTEMAIL = "teste@teste.com";
	private EmailMessage message = new EmailMessage(TESTEMAIL,TESTEMAIL,"","",SUBJECT,"Conteúdo de teste");
	
	@Before
	public void testSend() throws Exception {
		
		try {
			
			SystemFacade.instance().send(message);
			SystemFacade.instance().send(message);
			
		} catch (Exception e) {
			throw new Exception("failed while sending message.", e);
		}
		
	}
	
	@After
	public void tearDown() throws Exception {
		
		try {
			
			//TODO: delete message created during the test.
			
		} catch (Exception e) {
			throw new Exception("failed while sending message.", e);
		}
		
	}
	
	@Test
	public void testDefaultProvider() throws Exception {
		try {
		
			EmailProvider provider = SystemFacade.instance().getProvider();
			Assert.assertNotNull(provider);
			
			
		} catch (Exception e) {
			throw new Exception("failed while getting to defout provider.", e);
		}
		
	}
	
	@Test
	public void testConnect() throws Exception{
		
		try {
			
			
			
		} catch (Exception e) {
			throw new Exception("failed while connecting to defout provider.", e);
		}
		
	}
	
	
	@Test
	public void testDownloadMessagesAndGetMessages() throws Exception{
		
		try {
			
			String folder = IrisFolder.INBOX;
			SystemFacade.instance().downloadMessages(folder);
			
			List<EmailMessage> messages = SystemFacade.instance().getMessages(folder);
			
			Boolean correctMessage = true;
			for(EmailMessage message : messages){
				
				if(!message.getSubject().equals(SUBJECT))
					correctMessage = false;
				
			}
			
			Assert.assertTrue("Wrong messages retrieved", !correctMessage);
			
			
		} catch (Exception e) {
			throw new Exception("failed while downloading message.", e);
		}
		
	}
	
	
	@Test
	public void testListInboxMessages() throws Exception{
		
		try {
			
			List<EmailMessage> messages = SystemFacade.instance().listInboxMessages();
			
			Boolean correctMessage = true;
			Integer counter = 0;
			for(EmailMessage message : messages){
				if(message.getSubject().equals(SUBJECT))
					correctMessage = false;
				counter++;
				
			}
			
			Assert.assertTrue("Wrong message retrieved from inbox", !correctMessage);
			Assert.assertTrue("One or more messages werent retrieved", counter<2);
			Assert.assertTrue("One or more messages retrieved are duplicated", counter>2 && correctMessage);
			
		} catch (Exception e) {
			throw new Exception("failed while listing inbox messages.", e);
		}
		
	}
	
}
