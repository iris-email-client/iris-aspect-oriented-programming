package br.unb.cic.iris.persistence;

import br.unb.cic.iris.core.exception.DBException;
import br.unb.cic.iris.core.model.IrisFolder;

public interface IFolderDAO {
	
	public void save(IrisFolder folder) throws DBException;
	
	public IrisFolder findByName(String folderName) throws DBException;
	
}
