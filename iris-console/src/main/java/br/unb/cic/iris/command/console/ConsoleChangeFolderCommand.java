package br.unb.cic.iris.command.console;
		
import br.unb.cic.iris.command.AbstractMailCommand;
import br.unb.cic.iris.core.FolderManager;
import br.unb.cic.iris.core.model.IrisFolder;
import br.unb.cic.iris.core.exception.EmailException;
		
public class ConsoleChangeFolderCommand extends AbstractMailCommand {
	static final String COMMAND_NAME = "cd";
		
	@Override
	public void explain() {
		System.out.println("(cd <id_folder>) - change current folder)");
	}
		
	@Override
	public void handleExecute() throws EmailException {
		if(validParameters()){
			IrisFolder folder = FolderManager.instance().changeToFolder(parameters[0]);
			//String folder = FolderManager.instance().getCurrentFolderName();
			System.out.println(folder.getName());
		}else{
			//TODO: invalid parameters given.
		}
	}
		
	@Override
	public String getCommandName() {
		return COMMAND_NAME;
	}
}