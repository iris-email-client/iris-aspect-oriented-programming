package br.unb.cic.iris.persistence.sqlite3;

import br.unb.cic.iris.persistence.IEmailDAO;
import br.unb.cic.iris.persistence.IFolderDAO;

public class DAOFactory implements br.unb.cic.iris.persistence.IDAOFactory {

	private static DAOFactory instance;
	
	private DAOFactory() { }
	
	public static DAOFactory instance() {
		if(instance == null) {
			instance = new DAOFactory();
		}
		return instance;
	}
	

	public IEmailDAO createEmailDAO() {
		return EmailDAO.instance();
	}

	public IFolderDAO createFolderDAO() {
		return FolderDAO.instance();
	}
	

}
