package br.unb.cic.iris.persistence.sqlite3;

import br.unb.cic.iris.persistence.DAOFactory;
import br.unb.cic.iris.persistence.IAddressBookDAO;
import br.unb.cic.iris.persistence.IEmailDAO;
import br.unb.cic.iris.persistence.IFolderDAO;

public class SQLiteDAOFactory implements DAOFactory {

	private static SQLiteDAOFactory instance;
	
	private SQLiteDAOFactory() { }
	
	public static SQLiteDAOFactory instance() {
		if(instance == null) {
			instance = new SQLiteDAOFactory();
		}
		return instance;
	}
	
	public IAddressBookDAO createAddressBookDAO() {
		return AddressBookDAO.instance();
	}

	public IEmailDAO createEmailDAO() {
		return EmailDAO.instance();
	}

	public IFolderDAO createFolderDAO() {
		return FolderDAO.instance();
	}
	
//	public ITagDAO createTagDAO() {
//		return TagDAO.instance();
//	}

}