package br.unb.cic.iris.persistence.lucene;

import br.unb.cic.iris.persistence.ITagDAO;

public privileged aspect FeatureTagDAO {
	
	public interface ITagDAOFactory {
		public ITagDAO  createTagDAO();
	}

	public ITagDAO ITagDAOFactory.createTagDAO() {
		return TagDAO.instance();
	}
	
	declare parents: br.unb.cic.iris.persistence.IDAOFactory implements ITagDAOFactory; 
	
	
}
