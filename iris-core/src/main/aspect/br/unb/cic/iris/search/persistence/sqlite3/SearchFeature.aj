package br.unb.cic.iris.search.persistence.sqlite3;

import java.util.ArrayList;
import java.util.List;

import br.unb.cic.iris.core.exception.DBException;
import br.unb.cic.iris.core.exception.EmailException;
import br.unb.cic.iris.core.model.EmailMessage;
import br.unb.cic.iris.persistence.sqlite3.EmailDAO;
import br.unb.cic.iris.search.Searcher;


public aspect SearchFeature {
	declare parents: br.unb.cic.iris.core.SystemFacade implements Searcher;

	public List<EmailMessage> br.unb.cic.iris.core.SystemFacade.search(
			String query) throws EmailException {
		EmailDAO dao = EmailDAO.instance();
		return dao.search(query);
	}

	declare parents: br.unb.cic.iris.persistence.sqlite3.EmailDAO implements Searcher;

	public List<EmailMessage> br.unb.cic.iris.persistence.sqlite3.EmailDAO.search(
			String query) throws EmailException {
		List<EmailMessage> result = new ArrayList<EmailMessage>();
		return result;
	}
}
