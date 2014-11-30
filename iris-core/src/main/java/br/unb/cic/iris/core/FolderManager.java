package br.unb.cic.iris.core;

import java.util.List;

import br.unb.cic.iris.core.exception.EmailException;
import br.unb.cic.iris.core.model.EmailMessage;
import br.unb.cic.iris.core.model.IrisFolder;

public final class FolderManager extends Manager {

	private static final FolderManager instance = new FolderManager();
	private static final String ROOT_FOLDER = "ROOT";
	private IrisFolder currentFolder = new IrisFolder(ROOT_FOLDER);

	private List<EmailMessage> currentMessages;

	private int pageNumber = 0;
	private int pageSize = 10;

	private FolderManager() { }

	public static FolderManager instance() {
		return instance;
	}

	public String getCurrentFolderName() {
		return currentFolder.getName();
	}
	
	public List<EmailMessage> getCurrentMessages(){
		return currentMessages;
	}

	public void changeToFolder(Integer folderId) throws EmailException {
		changeToFolder(folderId+"");
	}
	
	public IrisFolder changeToFolder(String folderId) throws EmailException {
		IrisFolder folder = getDaoFactory().createFolderDAO().findById(folderId);
		if(folder != null) {
			currentFolder = folder;
			currentMessages = new java.util.ArrayList<EmailMessage>();
		}
		else {
			System.out.println("not found");
		}
		return folder;
	}
	
	public List<IrisFolder> listFolders() throws EmailException {
		return getDaoFactory().createFolderDAO().findAll();
	}
	
	public List<EmailMessage> listFolderMessages() throws EmailException {
		if(currentFolder == null || currentFolder.getId() == null) {
			return new java.util.ArrayList<EmailMessage>();
		}
		//return EmailDAO.instance().listMessages(currentFolder.getId());
		//TODO ver como ficara a paginacao
		currentMessages = getDaoFactory().createEmailDAO().findByFolder(currentFolder.getId());
		return currentMessages; //currentMessages.subList(pageSize * pageNumber, (pageSize * (pageNumber + 1)) - 1);
	}
	
	public List<EmailMessage> next() throws EmailException {
		if(pageNumber < currentMessages.size() / pageSize) {
			pageNumber++;
		}
		return listFolderMessages();
	}
	
	public List<EmailMessage> previous() throws EmailException {
		if(pageNumber > 0) {
			pageNumber--;
		}
		return listFolderMessages();
	}
	
	public EmailMessage getMessage(String id) throws EmailException {
		return getDaoFactory().createEmailDAO().findById(id);
	}
	
}
