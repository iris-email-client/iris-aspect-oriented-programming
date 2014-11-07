package br.unb.cic.iris.core.addressbook;

import java.util.List;
import br.unb.cic.iris.core.model.AddressBookEntry;
import br.unb.cic.iris.core.exception.EmailException;
import br.unb.cic.iris.persistence.sqlite3.AddressBookDAO;

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
}
