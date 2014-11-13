package br.unb.cic.iris.core.addressbook;

import java.util.List;

import br.unb.cic.iris.core.model.AddressBookEntry;
import br.unb.cic.iris.core.model.EmailMessage;
import br.unb.cic.iris.core.exception.EmailException;
import br.unb.cic.iris.persistence.sqlite3.AddressBookDAO;
import br.unb.cic.iris.util.EmailValidator;

/**
 * An AddressBook implementation module  
 * using AOP. 
 * 
 * @author rbonifacio
 */
public aspect FeatureAddressBook {

	/**
	 * Using this aspects, we claim that SystemFacade implements
	 * AddressBookFacade, an interface that would exist only in the cases where
	 * the AddressBook feature has been selected.
	 */
	declare parents: br.unb.cic.iris.core.SystemFacade implements AddressBookFacade;

	// Variability approach: intertype declarations. Using
	// AOP we are able to introduce new methods
	// related to the AddressBook feature into SystemFacade.

	/*
	 * @see
	 * br.unb.cic.iris.core.addressbook.AddressBookFacade.addAddressBookEntry
	 * (String, String) throws EmailException
	 */
	public void br.unb.cic.iris.core.SystemFacade.addAddressBookEntry(String name, String email) throws EmailException {
		System.out.println("saving ab from AOP");
		AddressBookDAO dao = AddressBookDAO.instance();
		dao.save(new AddressBookEntry(name, email));
	}
	
	public void br.unb.cic.iris.core.SystemFacade.deleteAddressBookEntry(String name) throws EmailException {
		AddressBookDAO dao = AddressBookDAO.instance();
		dao.delete(name);
	}

	public List<AddressBookEntry> br.unb.cic.iris.core.SystemFacade.listAddressBook() throws EmailException {
		return AddressBookDAO.instance().findAll();
	}
	
	//there is no quantification here. 
	pointcut sendMessage(EmailMessage message) : execution(void br.unb.cic.iris.mail.EmailClient.send(EmailMessage)) && args(message);
	
	Object around(EmailMessage message) throws EmailException : sendMessage(message) {
		System.out.println("Enviando mensagem por AOP");
		message.setTo(findAddress(message.getTo()));
		message.setCc(findAddress(message.getCc()));
		message.setBcc(findAddress(message.getBcc()));
		
		return proceed(message);
	}
	
	//if an email address is not a valid email, it might be 
	//an address book entry. in this case, we return the corresponding 
	//email address from the address book.
	private String findAddress(String emailAddress) throws EmailException {
		if(emailAddress != null && !EmailValidator.validate(emailAddress)){
			AddressBookEntry entry = AddressBookDAO.instance().find(emailAddress);
			
			return entry == null ? null : entry.getAddress();
		}
		return emailAddress;
	}
}
