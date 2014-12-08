package br.unb.cic.iris.tag.persistence;

import java.util.logging.Logger;

import br.unb.cic.iris.persistence.InjectDAOFactory;

public aspect InjectTagDAO {

	private static final String BASE_PACKAGE = "br.unb.cic.iris.tag.persistence";
	private static final String CLASS =  "TagDAO";
	
	public interface ITagDAOFactory {
		public ITagDAO createTagDAO();
	}
	
	public ITagDAO ITagDAOFactory.createTagDAO() {
		try {
			String name = BASE_PACKAGE + "." + InjectDAOFactory.persistenceType() + "." + CLASS;
			Class factory = Class.forName(name);
			return (ITagDAO)factory.getMethod("instance").invoke(null);
		}
		catch(Exception e) {
			Logger.getLogger(InjectDAOFactory.class.getName()).severe("Could not instantiate DAOFactory");
			e.printStackTrace();
			throw new RuntimeException();
		}
	}
	
	declare parents:  br.unb.cic.iris.persistence.IDAOFactory implements ITagDAOFactory;
	
}
