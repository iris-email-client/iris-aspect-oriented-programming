package br.unb.cic.iris.core.addressbook;

import java.util.List;

import br.unb.cic.iris.core.exception.EmailException;
import br.unb.cic.iris.core.model.AddressBookEntry;

public interface AddressBookFacade {
	/**
	 * saves a new address book entry
	 * @param name the nick name
	 * @param email the related email address
	 */
	public void addAddressBookEntry(String name, String email) throws EmailException;
	
	/**
	 * deletes an existing address book entry record from the database
	 * 
	 * @param name the nick name
	 */
	public void deleteAddressBookEntry(String name) throws EmailException;

	/**
	 * lists all address book entries
	 * @return the list of address book entries
	 */
	public List<AddressBookEntry> listAddressBook() throws EmailException;
}
