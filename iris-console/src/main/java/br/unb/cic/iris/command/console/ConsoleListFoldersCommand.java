package br.unb.cic.iris.command.console;

import static br.unb.cic.iris.i18n.Message.message;

import java.util.List;

import br.unb.cic.iris.command.AbstractMailCommand;
import br.unb.cic.iris.core.FolderManager;
import br.unb.cic.iris.core.exception.EmailException;
import br.unb.cic.iris.core.model.IrisFolder;

public class ConsoleListFoldersCommand extends AbstractMailCommand {
	static final String COMMAND_NAME = "lf";

	@Override
	public void explain() {
		System.out.printf("(%s) - %s %n", COMMAND_NAME, message("command.list.folders.explain"));
	}

	@Override
	public void handleExecute() throws EmailException {
		List<IrisFolder> irisFolders = FolderManager.instance().listFolders();
		for (IrisFolder folder : irisFolders) {
			System.out.println(" + " + folder.getName() + " (" + folder.getId() + " )");
		}
	}

	@Override
	public String getCommandName() {
		return COMMAND_NAME;
	}

}
