package br.unb.cic.iris.persistence;

import java.io.FileInputStream;
import java.util.Properties;
import java.util.logging.Logger;

import br.unb.cic.iris.persistence.sqlite3.SQLiteDAOFactory;

/**
 * This aspect is responsible for setting the proper persistence 
 * factory into the system facade. 
 *  
 * @author rbonifacio
 */
public privileged aspect PersistenceFeature {

	after(br.unb.cic.iris.core.SystemFacade facade) : initialization(br.unb.cic.iris.core.SystemFacade.new(..)) && 	this(facade) {
		String fileName = "persistence.properties";
		try {
			Logger.getLogger(PersistenceFeature.class.getName()).info("Load properties" + fileName);
			Properties properties = new Properties();
			properties.load(getClass().getResourceAsStream("/" + fileName));
			String name = properties.getProperty("factory");
			if(name != null) {
				Class factory = Class.forName(name);
				facade.daoFactory = ((DAOFactory)factory.newInstance());
			}
		}
		catch(Exception e) {
			Logger.getLogger(PersistenceFeature.class.getName()).warning("Using default DAOFactory");			
			facade.daoFactory = new SQLiteDAOFactory();
		}	
	}
}
