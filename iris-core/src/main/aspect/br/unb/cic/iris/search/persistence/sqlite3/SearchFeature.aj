package br.unb.cic.iris.search.persistence.sqlite3;

import java.util.ArrayList;
import java.util.List;

import br.unb.cic.iris.core.exception.DBException;
import br.unb.cic.iris.core.exception.EmailException;
import br.unb.cic.iris.core.model.EmailMessage;
import br.unb.cic.iris.persistence.sqlite3.EmailDAO;
import br.unb.cic.iris.search.Searcher;


public privileged aspect SearchFeature {
	declare parents: br.unb.cic.iris.core.SystemFacade implements Searcher;

	public List<EmailMessage> br.unb.cic.iris.core.SystemFacade.search(
			String query) throws EmailException {
		EmailDAO dao = EmailDAO.instance();
		return dao.search(query);
	}

	declare parents: br.unb.cic.iris.persistence.sqlite3.EmailDAO implements Searcher;

	private static final String EmailDAO.FIND = "select e FROM EmailMessage e where e.from like :text or e.to like :text or e.cc like :text or e. cc like :text or e.subject like :text or e.message like :text";

	public List<EmailMessage> br.unb.cic.iris.persistence.sqlite3.EmailDAO.search(
			String query) throws EmailException {
		List<EmailMessage> result = new ArrayList<EmailMessage>();
		try {
			startSession(false);
			result = session.createQuery(FIND).setParameter("text","%" +  query + "%").list();
		}
		finally {
			closeSession();
		}
		return result;
	}
}
