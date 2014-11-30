package br.unb.cic.iris.command.console;
		
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import br.unb.cic.iris.command.AbstractMailCommand;
import br.unb.cic.iris.core.FolderManager;
import br.unb.cic.iris.core.exception.EmailException;
import br.unb.cic.iris.core.model.EmailMessage;
	
public class ConsoleListMessagesCommand extends AbstractMailCommand {
	static final String COMMAND_NAME = "ls";
	DateFormat formatter = new SimpleDateFormat("dd/MMM/yy 'at' HH:mm");
	List<EmailMessage> messages;
			
	@Override
	public void explain() {
		System.out.println("(ls) - list messages from current folder (local database)");
	}
	@Override
	public void handleExecute() throws EmailException {
		messages = FolderManager.instance().listFolderMessages();
		for(int i=0; i < messages.size(); i++){
			EmailMessage msg = messages.get(i);
			System.out.printf("%d - %s - %s \t- %s%n", i+1, formatter.format(msg.getDate()), msg.getFrom(), msg.getSubject());
		}
	}
	@Override
	public String getCommandName() {
		return COMMAND_NAME;
	}
}
	
