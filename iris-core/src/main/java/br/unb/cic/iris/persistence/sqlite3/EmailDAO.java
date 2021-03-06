/*
 * EmailDAO.java
 * ---------------------------------
 *  version: 0.0.1
 *  date: Sep 18, 2014
 *  author: rbonifacio
 *  list of changes: (none) 
 */
package br.unb.cic.iris.persistence.sqlite3;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.unb.cic.iris.core.exception.DBException;
import br.unb.cic.iris.core.model.EmailMessage;
import br.unb.cic.iris.persistence.IEmailDAO;

/**
 * An implementation of @see br.unb.cic.iris.persistence.EmailDAO using the SQLite databaese.
 * 
 * @author rbonifacio
 *
 */
public final class EmailDAO extends AbstractDAO<EmailMessage> implements IEmailDAO {
	private static final String FIND_MAX_DATE = "select max(e.date) FROM EmailMessage e";
	private static final String FIND_BY_FOLDER = "select e FROM EmailMessage e where e.folder.id = :pId";
	// select o from LoadFileHistory o where o.finishDate > :today

	/* the single instance of EmailDAO */
	private static EmailDAO instance = new EmailDAO();

	/* private constructor, according to the singleton pattern */
	private EmailDAO() {
	}

	/**
	 * Retrieves the singleton instance of EmailDAO.
	 * 
	 * @return the singleton instance of EmailDAO
	 */
	public static EmailDAO instance() {
		return instance;
	}

	@Override
	public void saveMessage(EmailMessage message) throws DBException {
		super.saveOrUpdate(message);
	}

	@Override
	public Date lastMessageReceived() throws DBException {
		Date date = null;
		try {
			startSession(false);
			date = (Date) session.createQuery(FIND_MAX_DATE).uniqueResult();
		} finally {
			closeSession();
		}
		return date;
	}

	public List<EmailMessage> findByFolder(String inbox) {
		List<EmailMessage> messages = new ArrayList<EmailMessage>();
		try {
			startSession(false);
			messages = session.createQuery(FIND_BY_FOLDER).setParameter("pId", inbox).list();
		}
		finally {
			closeSession();
		}
		return messages; 
	}


}
