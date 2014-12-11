package br.unb.cic.iris.command.console;
		
import java.util.List;

import br.unb.cic.iris.core.FolderManager;
import br.unb.cic.iris.core.exception.EmailException;
import br.unb.cic.iris.core.model.EmailMessage;
	
public class ConsoleListMessagesCommand extends AbstractListMessagesCommand {
	static final String COMMAND_NAME = "ls";
	List<EmailMessage> messages;
			
	@Override
	public void explain() {
		System.out.println("(ls) - list messages from current folder (local database)");
	}
	@Override
	public void handleExecute() throws EmailException {
		messages = FolderManager.instance().listFolderMessages();
		print(messages);
	}
	@Override
	public String getCommandName() {
		return COMMAND_NAME;
	}
}
	
