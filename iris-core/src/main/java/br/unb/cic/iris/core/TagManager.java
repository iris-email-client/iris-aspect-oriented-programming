package br.unb.cic.iris.core;

import java.util.List;

import br.unb.cic.iris.core.model.EmailMessage;
import br.unb.cic.iris.core.model.Tag;

/**
 * A dummy class to compile the program. 
 * @TODO: Daniel Sandoval is working on this class 
 * in another branch. Remove this comment later. 
 */

public class TagManager extends Manager {
	
	private static TagManager instance;
	
	private TagManager() {}
	
	public static TagManager instance() {
		if(instance == null) {
			instance = new TagManager();
		}
		return instance;
	}

	public List<EmailMessage> listMessagesByTag(String tag) {
		// TODO Auto-generated method stub
		throw new RuntimeException("TagManager.listMessagesByTag(...) - not implemented yet!");
	}

	public void saveTags(String id, String tags) {
		// TODO Auto-generated method stub
		throw new RuntimeException("TagManager.saveTags(...) - not implemented yet!");
	}

	public List<Tag> findAll() {
		// TODO Auto-generated method stub
		throw new RuntimeException("TagManager.findAll(...) - not implemented yet!");
	}
}
