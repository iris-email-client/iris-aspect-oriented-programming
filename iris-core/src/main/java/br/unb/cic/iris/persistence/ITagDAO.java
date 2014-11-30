package br.unb.cic.iris.persistence;

import java.util.List;

import br.unb.cic.iris.core.exception.DBException;
import br.unb.cic.iris.core.model.EmailMessage;
import br.unb.cic.iris.core.model.Tag;

public interface ITagDAO {
	
	public Tag findOrCreateByName(String name) throws DBException;
	
	public List<Tag> findTagsByEmailMessage(EmailMessage message) throws DBException;
	
	public void saveOrUpdate(Tag tag) throws DBException;
	
	public void delete(Tag tag) throws DBException;

}
