package br.unb.cic.iris.command.console;

import br.unb.cic.iris.command.AbstractMailCommand;
import br.unb.cic.iris.core.FolderManager;
import br.unb.cic.iris.core.exception.EmailException;

public class ConsoleCurrentFolderCommand extends AbstractMailCommand {
	static final String COMMAND_NAME = "pwd";
	
	@Override
	public void explain() {
		System.out.println("(pwd) - show current folder)");
	}
	
	@Override
	public void handleExecute() throws EmailException {
		System.out.println(FolderManager.instance().getCurrentFolderName());
	}
	
	@Override
	public String getCommandName() {
		return COMMAND_NAME;
	}

}
