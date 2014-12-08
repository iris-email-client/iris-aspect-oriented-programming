package br.unb.cic.iris.core;

import br.unb.cic.iris.persistence.IDAOFactory;

/**
 * An abstract class that defines a contract 
 * to all manager classes. This contract says that every 
 * manager must have a daoFactory, which might be injected 
 * some how (using AOP capabilities, for instance). 
 *  
 * @author rbonifacio
 */
public abstract class Manager {
	private IDAOFactory daoFactory;

	public IDAOFactory getDaoFactory() {
		return daoFactory;
	}

	public void setDaoFactory(IDAOFactory daoFactory) {
		this.daoFactory = daoFactory;
	}
}
