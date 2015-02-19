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
public privileged aspect InjectDAOFactory {

	private static final String BASE_PACKAGE = "br.unb.cic.iris.persistence";
	private static final String CLASS = "DAOFactory";
	
	public static String persistenceType() {
		String fileName = "persistence.properties";
		try {	
			Logger.getLogger(InjectDAOFactory.class.getName()).config("Load properties" + fileName);
			Properties properties = new Properties();
			properties.load(InjectDAOFactory.class.getResourceAsStream("/" + fileName));
			return properties.getProperty("factory");
		}
		catch(Exception e) {		
			throw new RuntimeException("Could not read properties from " + fileName);
		}
	}
	

	//----------------------------------------------------
	//That's is a nice pointcut.
	//It intercepts the execution of getDaoFactory method, 
	//within any subclass of Manager.
	//----------------------------------------------------
	
	pointcut injectDaoFactory() : execution (br.unb.cic.iris.persistence.IDAOFactory br.unb.cic.iris.core.Manager+.getDaoFactory()); 

	//In a first moment, I was wondering that using AOP for injecting 
	//the DAOFactory implementation would be a *napalm bomb* to kill a 
	//single, inofencive ant. However, after I have implemented (or copied) 
	//FolderManager (from delta implementation), I realized how AOP is 
	//useful in this context. We have quantification now, which would be 
	//a little difficult to implement using OOP--- at least without a framework 
	//like spring. Ok, but spring uses AOP internally. 
	Object around() : injectDaoFactory() {
		
		System.out.println(thisJoinPoint.getTarget());
		try {
			String name = BASE_PACKAGE + "." + persistenceType() + "." + CLASS;
			Logger.getLogger(InjectDAOFactory.class.getName()).config("working with factory: " + name);
			if(name != null) {
				Class factory = Class.forName(name);
				return factory.getMethod("instance").invoke(null);
				//return ((DAOFactory)factory.newInstance());
			}
			else {
				Logger.getLogger(InjectDAOFactory.class.getName()).severe("Could not instantiate DAOFactory. Factory name is null");			
				throw new RuntimeException();
			}
		}
		catch(Exception e) {
			Logger.getLogger(InjectDAOFactory.class.getName()).severe("Could not instantiate DAOFactory");
			throw new RuntimeException();
		}
	}
}
