package br.unb.cic.iris.persistence.lucene;

import java.io.File;

import br.unb.cic.iris.core.model.IrisFolder;
import br.unb.cic.iris.persistence.DAOFactory;
import br.unb.cic.iris.persistence.IAddressBookDAO;
import br.unb.cic.iris.persistence.IEmailDAO;
import br.unb.cic.iris.persistence.IFolderDAO;

public class LuceneDAOFactory implements DAOFactory {

	// Don't forget to mkdir "~/.iris/lucene_idx".
	public static final String INDEX_DIR = System.getProperty("user.home") + "/.iris/lucene_idx/";

	private static LuceneDAOFactory instance;
	
	private LuceneDAOFactory () {
		File path = new File(INDEX_DIR);
		if (!path.exists()) {
			boolean status = path.mkdir();
			if (!status)
				System.exit(0); // "Could not create directory: '" + INDEX_DIR + "'."
		} else if (path.isDirectory()) {
			try {
				if (path.list().length > 0) { // Index already exists
					IndexManager.setIndex(INDEX_DIR);
				} else { // Index has to be created
					IndexManager.createIndex(INDEX_DIR);
					
					// Creates the standard folders in the index
					IFolderDAO folderDAO = createFolderDAO();
					folderDAO.saveOrUpdate(new IrisFolder("INBOX"));
					folderDAO.saveOrUpdate(new IrisFolder("OUTBOX"));
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(0);
			}
		}
	}
	
	public static LuceneDAOFactory instance() {
		if (instance == null)
			instance = new LuceneDAOFactory();
		
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
