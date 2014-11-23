package br.unb.cic.iris.persistence.lucene;

import br.unb.cic.iris.persistence.DAOFactory;
import br.unb.cic.iris.persistence.IAddressBookDAO;
import br.unb.cic.iris.persistence.IEmailDAO;
import br.unb.cic.iris.persistence.IFolderDAO;

import br.unb.cic.iris.persistence.lucene.AddressBookDAO;
import br.unb.cic.iris.persistence.lucene.EmailDAO;
import br.unb.cic.iris.persistence.lucene.FolderDAO;


public class LuceneDAOFactory implements DAOFactory {

	public IAddressBookDAO createAddressBookDAO() {
		return AddressBookDAO.instance();
	}

	public IEmailDAO createEmailDAO() {
		return EmailDAO.instance();
	}

	public IFolderDAO createFolderDAO() {
		return FolderDAO.instance();
	}

}
