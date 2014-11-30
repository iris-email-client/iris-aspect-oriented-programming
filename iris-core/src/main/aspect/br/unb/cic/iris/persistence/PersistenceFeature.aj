package br.unb.cic.iris.persistence;

import java.util.Properties;
import java.util.logging.Logger;

//import br.unb.cic.iris.persistence.sqlite3.SQLiteDAOFactory;

/**
 * This aspect is responsible for setting the proper persistence 
 * factory into the system facade (actually, in all manager 
 * classes that depends on a DAOFactory). 
 *  
 * @author rbonifacio
 */
public privileged aspect PersistenceFeature {

	//----------------------------------------------------
	//That's is a nice pointcut.
	//It intercepts the execution of getDaoFactory method, 
	//within any subclass of Manager.
	//----------------------------------------------------
	
	pointcut injectDaoFactory() : execution (DAOFactory br.unb.cic.iris.core.Manager+.getDaoFactory()); 

	//In a first moment, I was wondering that using AOP for injecting 
	//the DAOFactory implementation would be a *napalm bomb* to kill a 
	//single, inofencive ant. However, after I have implemented (or copied) 
	//FolderManager (from delta implementation), I realized how AOP is 
	//useful in this context. We have quantification now, which would be 
	//a little difficult to implement using OOP--- at least without a framework 
	//like spring. Ok, but spring uses AOP internally. 
	Object around() : injectDaoFactory() {
		System.out.println(thisJoinPoint.getTarget());
		String fileName = "persistence.properties";
		try {
			Logger.getLogger(PersistenceFeature.class.getName()).info("Load properties" + fileName);
			Properties properties = new Properties();
			properties.load(getClass().getResourceAsStream("/" + fileName));
			String name = properties.getProperty("factory");
			Logger.getLogger(PersistenceFeature.class.getName()).info("working with factory: " + name);
			if(name != null) {
				Class factory = Class.forName(name);
				return factory.getMethod("instance").invoke(null);
				//return ((DAOFactory)factory.newInstance());
			}
			else {
				Logger.getLogger(PersistenceFeature.class.getName()).severe("Could not instantiate DAOFactory. Factory name is null");			
				throw new RuntimeException();
			}
		}
		catch(Exception e) {
			Logger.getLogger(PersistenceFeature.class.getName()).severe("Could not instantiate DAOFactory");
			e.printStackTrace();
			throw new RuntimeException();
		}
	}
}
