package br.unb.cic.iris.persistence.sqlite3;

import br.unb.cic.iris.persistence.ITagDAO;

public privileged aspect FeatureTagDAO {
	public ITagDAO  br.unb.cic.iris.persistence.DAOFactory.createTagDAO() {
		return TagDAO.instance();
	}
}
