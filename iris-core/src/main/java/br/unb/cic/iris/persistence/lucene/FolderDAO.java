package br.unb.cic.iris.persistence.lucene;

import org.apache.lucene.document.Document;

import br.unb.cic.iris.core.exception.DBException;
import br.unb.cic.iris.core.model.IrisFolder;
import br.unb.cic.iris.persistence.IFolderDAO;

public class FolderDAO extends LuceneDoc<IrisFolder> implements IFolderDAO {

	public IrisFolder findByName(String folderName) throws DBException {
		// TODO Not implemented yet!
		return null;
	}
	
	@Override
	protected Document toLuceneDoc(IrisFolder folder) {
		// TODO Implement 1:N relationship!
		return null;
	}

	@Override
	protected IrisFolder fromLuceneDoc(Document doc) {
		// TODO Auto-generated method stub
		return null;
	}

}
