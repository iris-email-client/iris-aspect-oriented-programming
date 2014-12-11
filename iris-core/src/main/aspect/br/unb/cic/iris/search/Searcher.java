package br.unb.cic.iris.search;

import java.util.List;

import br.unb.cic.iris.core.exception.EmailException;
import br.unb.cic.iris.core.model.EmailMessage;

public interface Searcher {
	List<EmailMessage> search(String query) throws EmailException;
}
