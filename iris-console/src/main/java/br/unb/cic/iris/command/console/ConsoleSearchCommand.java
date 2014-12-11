package br.unb.cic.iris.command.console;

import static br.unb.cic.iris.i18n.Message.message;

import java.util.List;

import br.unb.cic.iris.core.SystemFacade;
import br.unb.cic.iris.core.exception.EmailException;
import br.unb.cic.iris.core.model.EmailMessage;

public class ConsoleSearchCommand extends AbstractListMessagesCommand {
	
	public static final String COMMAND_SEARCH = "search";

	@Override
	public void explain() {
		System.out.printf("(%s <query>) - %s %n", COMMAND_SEARCH,
				message("command.search.explain"));		
	}

	@Override
	public String getCommandName() {
		return COMMAND_SEARCH;
	}

	@Override
	protected void handleExecute() throws EmailException {
		if (validParameters()) {
			String query = "";
			for (String parameter : parameters) {
				if (query.length() > 0) query += " ";
				query += parameter;
			}
			List<EmailMessage> emails = SystemFacade.instance().search(query);
			print(emails);
		}
	}
}