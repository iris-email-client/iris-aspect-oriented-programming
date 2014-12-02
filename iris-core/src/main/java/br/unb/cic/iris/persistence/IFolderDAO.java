package br.unb.cic.iris.persistence;

import java.util.List;

import br.unb.cic.iris.core.exception.DBException;
import br.unb.cic.iris.core.model.IrisFolder;

public interface IFolderDAO {
	public IrisFolder findById(String uuid) throws DBException; 
	
	public void saveOrUpdate(IrisFolder folder) throws DBException;
	
	public IrisFolder findByName(String folderName) throws DBException;
	
	public List<IrisFolder> findAll() throws DBException;

	public void delete(IrisFolder folder) throws DBException;
}
