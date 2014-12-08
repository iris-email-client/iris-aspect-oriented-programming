package br.unb.cic.iris.addressbook.persistence.sqlite3;

import java.util.logging.Logger;

import org.hibernate.cfg.Configuration;

import br.unb.cic.iris.addressbook.model.AddressBookEntry;

public aspect MapAddressBook {

	pointcut configure(Configuration c) :  call (Configuration Configuration.configure()) && target(c);
	
	after(Configuration c) : configure(c) {
		Logger.getLogger(MapAddressBook.class.getName()).severe("Mapping AddressBookEntry to Hibernate CFG");
		c.addAnnotatedClass(AddressBookEntry.class);
	}
}
