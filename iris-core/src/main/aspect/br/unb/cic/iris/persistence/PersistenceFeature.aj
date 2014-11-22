package br.unb.cic.iris.persistence;

import br.unb.cic.iris.persistence.sqlite3.SQLiteDAOFactory;

public privileged aspect PersistenceFeature {

	after(br.unb.cic.iris.core.SystemFacade facade) : initialization(br.unb.cic.iris.core.SystemFacade.new(..)) && 	this(facade) {
		facade.daoFactory = new SQLiteDAOFactory();
	}
}
