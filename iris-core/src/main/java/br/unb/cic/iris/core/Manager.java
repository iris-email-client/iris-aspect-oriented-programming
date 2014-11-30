package br.unb.cic.iris.core;

import br.unb.cic.iris.persistence.DAOFactory;

/**
 * An abstract class that defines a contract 
 * to all manager classes. This contract says that every 
 * manager must have a daoFactory, which might be injected 
 * some how (using AOP capabilities, for instance). 
 *  
 * @author rbonifacio
 */
public abstract class Manager {
	private DAOFactory daoFactory;

	public DAOFactory getDaoFactory() {
		return daoFactory;
	}

	public void setDaoFactory(DAOFactory daoFactory) {
		this.daoFactory = daoFactory;
	}
}
