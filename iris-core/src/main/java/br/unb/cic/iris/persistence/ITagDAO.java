package br.unb.cic.iris.persistence;

import java.util.List;

import br.unb.cic.iris.core.exception.DBException;
import br.unb.cic.iris.core.model.EmailMessage;
import br.unb.cic.iris.core.model.Tag;

public interface ITagDAO {
	
	public void saveOrUpdate(Tag tag) throws DBException;
	
	public Tag findOrCreateByName(String name) throws DBException;
	
	public List<Tag> findTagsByEmailMessage(String messageId) throws DBException;
	
	public List<Tag> findAll() throws DBException;
	
	public void addTagToMessage(String messageId, String tagName) throws DBException;

	public void delete(Tag tag) throws DBException;
}
