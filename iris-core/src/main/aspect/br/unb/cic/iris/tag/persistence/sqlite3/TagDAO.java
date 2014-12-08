package br.unb.cic.iris.tag.persistence.sqlite3;

import static br.unb.cic.iris.i18n.Message.message;

import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import br.unb.cic.iris.core.exception.DBException;
import br.unb.cic.iris.core.model.EmailMessage;
import br.unb.cic.iris.persistence.sqlite3.AbstractDAO;
import br.unb.cic.iris.persistence.sqlite3.EmailDAO;
import br.unb.cic.iris.tag.model.Tag;
import br.unb.cic.iris.tag.persistence.ITagDAO;

public final class TagDAO extends AbstractDAO<Tag> implements ITagDAO{

	//Singleton pattern
	private static TagDAO instance = new TagDAO();
	
	private TagDAO(){}
	
	public static TagDAO instance() {
		return instance;
	}
	
	//Hibernate Queries
	private static final String FIND_BY_NAME = "FROM Tag t WHERE t.name = :pName";
	private static final String FIND_BY_MESSAGE = "FROM Tag t WHERE :pMessage IN ELEMENTS(t.messages)";
	
	@Override
	public Tag findOrCreateByName(String name) throws DBException {
		try {
			startSession(true);
			Tag tag = (Tag) session.createQuery(FIND_BY_NAME).setParameter("pName", name).uniqueResult();
			if (tag == null) {
				tag = new Tag(name);
			}
			
			Set<EmailMessage> messages = tag.getMessages();
			Logger.getLogger(TagDAO.class.getName()).info(messages.size() + " message(s) related to " + name);
			return tag;
		} catch (Exception e) {
			throw new DBException(message("error.unknown.database.error"), e);
		}
		finally{
			closeSession();
		}
	}

	@Override
	public List<Tag> findTagsByEmailMessage(String messageId)
			throws DBException {
		
		try {
			EmailMessage message = EmailDAO.instance().findById(messageId);
			startSession(false);
			List<Tag> tags = (List<Tag>) session.createQuery(FIND_BY_MESSAGE).setParameter("pMessage", message).list();
			return tags;
		} catch (Exception e) {
			throw new DBException(message("error.unknown.database.error"), e);
		}
		finally{
			closeSession();
		}
	}
	
	@Override
	public void addTagToMessage(String messageId, String tagName) throws DBException {
		try {
			EmailMessage message = EmailDAO.instance().findById(messageId);
			startSession(true);
			Tag tag = new Tag(tagName);
			tag.getMessages().add(message);
			saveOrUpdate(tag);
		} catch (Exception e) {
			throw new DBException(message("error.unknown.database.error"), e);
		}
		finally {
			closeSession();
		}
	}
	
}
