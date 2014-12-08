package br.unb.cic.iris.persistence;

/**
 * An abstract factory for the persistence mechanism
 * 
 * @author rbonifacio
 */
public interface IDAOFactory {
	public IEmailDAO createEmailDAO();
	public IFolderDAO createFolderDAO();
}
