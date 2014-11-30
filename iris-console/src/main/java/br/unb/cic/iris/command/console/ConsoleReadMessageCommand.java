package br.unb.cic.iris.command.console;

import br.unb.cic.iris.command.AbstractMailCommand;
import br.unb.cic.iris.core.FolderManager;
import br.unb.cic.iris.core.exception.EmailException;
import br.unb.cic.iris.core.model.EmailMessage;

public class ConsoleReadMessageCommand extends AbstractMailCommand {
	static final String COMMAND_NAME = "read";
	
	@Override
	public void explain() {
		System.out.println("(read <id_msg>) - read specified message)");
	}
	
	@Override
	public void handleExecute() throws EmailException {
		if(validParameters()) {
			Integer idx = Integer.parseInt(parameters[0]);
			EmailMessage message = FolderManager.instance().getCurrentMessages().get(idx-1);
			//EmailMessage message = FolderManager.instance().getMessage(id);
			System.out.println("FROM: "+message.getFrom());
			System.out.println("DATE: "+message.getDate());
			System.out.println("SUBJECT: "+message.getSubject());
			System.out.println("CONTENT:\n"+message.getMessage());
		}
		else {
		}
	}
	
	@Override
	public String getCommandName() {
		return COMMAND_NAME;
	}
}