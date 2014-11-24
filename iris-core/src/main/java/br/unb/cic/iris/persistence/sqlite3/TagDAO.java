package br.unb.cic.iris.persistence.sqlite3;

import static br.unb.cic.iris.i18n.Message.message;

import java.util.List;

import br.unb.cic.iris.core.exception.DBException;
import br.unb.cic.iris.core.model.EmailMessage;
import br.unb.cic.iris.core.model.Tag;
import br.unb.cic.iris.core.model.TagName;
import br.unb.cic.iris.persistence.ITagDAO;

public final class TagDAO extends AbstractDAO<Tag> implements ITagDAO{

	//Singleton pattern
	private static TagDAO instance = new TagDAO();
	
	private TagDAO(){}
	
	public static TagDAO instance() {
		return instance;
	}
	
	//Hibernate Queries
	private static final String FIND_BY_NAME = "FROM TagName tName WHERE tName.name = :pName";
	private static final String FIND_BY_MESSAGE = "FROM Tag t WHERE t.message = :pMessage";
	
	@Override
	public Tag findOrCreateByName(String name) throws DBException {
		try {
			startSession();
			TagName tagName = (TagName) session.createQuery(FIND_BY_NAME).setParameter("pName", name).uniqueResult();
			if (tagName == null) {
				tagName = new TagName(name);
			}
			
			return new Tag(tagName);
		} catch (Exception e) {
			throw new DBException(message("error.unknown.database.error"), e);
		} finally {
			closeSession();
		}
	}

	@Override
	public List<Tag> findTagsByEmailMessage(EmailMessage message)
			throws DBException {
		
		try {
			startSession();
			List<Tag> tags = (List<Tag>) session.createQuery(FIND_BY_MESSAGE).setParameter("pMessage", message).list();
			return tags;
		} catch (Exception e) {
			throw new DBException(message("error.unknown.database.error"), e);
		} finally {
			closeSession();
		}
	}
	
}
