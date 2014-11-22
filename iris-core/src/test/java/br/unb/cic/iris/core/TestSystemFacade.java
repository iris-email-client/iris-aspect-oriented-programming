/*
 * TestSystemFacade.java
 * ---------------------------------
 *  version: 0.0.1
 *  date: Sep 18, 2014
 *  author: rbonifacio
 *  list of changes: (none) 
 */
package br.unb.cic.iris.core;

import org.junit.Assert;
import org.junit.Test;

import br.unb.cic.iris.core.model.AddressBookEntry;
import br.unb.cic.iris.mail.EmailProvider;

/**
 * @author ExceptionHandling
 *
 */
public class TestSystemFacade {

	@Test
	public void testDefaultProvider() {
		EmailProvider provider = SystemFacade.instance().getProvider();
		Assert.assertNotNull(provider);
	}
	
	@Test
	public void testSaveAddressBookEntry() {
		try {
			SystemFacade facade = SystemFacade.instance();
		
			AddressBookEntry entry = facade.find("rbonifacio");
			if(entry != null) {
				facade.deleteAddressBookEntry("rbonifacio");
			}
			
			System.out.println(" saving address book");
			facade.addAddressBookEntry("rbonifacio", "rbonifacio@cic.unb.br");
		
			entry = facade.find("rbonifacio");
			Assert.assertNotNull(entry);
			Assert.assertEquals("rbonifacio@cic.unb.br", entry.getAddress());
		}
		catch(Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
}
