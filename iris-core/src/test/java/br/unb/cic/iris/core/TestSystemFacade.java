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
import br.unb.cic.iris.mail.EmailProvider;


/**
 * @author ExceptionHandling
 *
 */
public class TestSystemFacade {

	private EmailProvider provider;
	private final static String SUBJECT = UUID.randomUUID().toString();
	private EmailMessage message = EmailMessage("teste@teste.com","teste@teste.com","teste@teste.com",SUBJECT,"Conteúdo de teste");
	
	@Before
	public void setup(){
		
		
		
	}
	
	@Test
	public void testDefaultProvider() throws Exception {
		try {
		
			EmailProvider provider = SystemFacade.instance().getProvider();
			Assert.assertNotNull(provider);
			
			
		} catch (Exception e) {
			throw new Exception("Faild while getting to defout provider.", e);
		}
		
	}
	
	@Test
	public void testConnect() throws Exception{
		
		try {
			
			
			
		} catch (Exception e) {
			throw new Exception("Faild while connecting to defout provider.", e);
		}
		
	}
	
	@Test
	public void testSend() throws Exception {
		
		try {
			
			SystemFacade.instance().send(message);
			
		} catch (Exception e) {
			throw new Exception("Faild while sending message.", e);
		}
		
	}
	
	@Test
	public void testDownloadMessages() throws Exception{
		
		try {
			
		} catch (Exception e) {
			throw new Exception("Faild while downloading message.", e);
		}
		
	}
	
	@Test
	public void testGetMessages() throws Exception{
		
		try {
			
			
			
		} catch (Exception e) {
			throw new Exception("Faild while sending message.", e);
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
			throw new Exception("Faild while listing inbox messages.", e);
		}
		
	}
	
}
