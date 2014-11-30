package br.unb.cic.iris.core;

import java.util.ArrayList;
import java.util.List;

import br.unb.cic.iris.core.exception.DBException;
import br.unb.cic.iris.core.model.EmailMessage;
import br.unb.cic.iris.core.model.Tag;
import br.unb.cic.iris.persistence.ITagDAO;

public class TagManager extends Manager {
	
	private static TagManager instance;
	
	private ITagDAO dao;
	
	private TagManager() {
		super();
		dao = getDaoFactory().createTagDAO();
	}
	
	public static TagManager instance() {
		if(instance == null) {
			instance = new TagManager();
		}
		return instance;
	}

	public List<EmailMessage> listMessagesByTag(String tag) {
		List<EmailMessage> messages;
		try {
			messages = new ArrayList<EmailMessage>(dao.findOrCreateByName(tag).getMessages());
			return messages;
		} catch (DBException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void saveTags(String id, String tags) {
		try {
			dao.addTagToMessage(id, tags);
		} catch (DBException e) {
			e.printStackTrace();
		}
	}

	public List<Tag> findAll() {
		try {
			return dao.findAll();
		} catch (DBException e) {
			e.printStackTrace();
		}
		return null;
	}
}
