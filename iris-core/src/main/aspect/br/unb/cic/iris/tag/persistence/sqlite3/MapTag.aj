package br.unb.cic.iris.tag.persistence.sqlite3;

import java.util.logging.Logger;

import org.hibernate.cfg.Configuration;


import br.unb.cic.iris.tag.model.Tag;

public aspect MapTag {

	pointcut configure(Configuration c) :  call (Configuration Configuration.configure()) && target(c);
	
	after(Configuration c) : configure(c) {
		Logger.getLogger(MapTag.class.getName()).severe("Mapping AddressBookEntry to Hibernate CFG");
		c.addAnnotatedClass(Tag.class);
	}
}
