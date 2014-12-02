package br.unb.cic.iris.persistence;

/**
 * An abstract factory for the persistence mechanism
 * 
 * @author rbonifacio
 */
public interface DAOFactory {
//	public IAddressBookDAO createAddressBookDAO();
	public IEmailDAO createEmailDAO();
	public IFolderDAO createFolderDAO();
//	public ITagDAO createTagDAO();
}
