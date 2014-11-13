package br.unb.cic.iris.persistence.lucene;

import java.io.IOException;
import java.util.Date;

import br.unb.cic.iris.core.exception.DBException;
import br.unb.cic.iris.core.model.EmailMessage;
import br.unb.cic.iris.core.model.IrisFolder;

public class TestLucene {

	static EmailMessage message;
	
	static {
		message = new EmailMessage();
		message.setFrom("alexandrelucchesi@gmail.com");
		message.setTo("rbonifacio123@gmail.com");
		message.setCc("jeremiasmg@gmail.com");
		message.setBcc("somebcc@gmail.com");
		message.setSubject("Alexandre Lucchesi");
		message.setMessage("Testing Lucene. :-)");
		message.setDate(new Date());
		message.setFolder(new IrisFolder(19, "UnB"));
	}
	
	private static boolean CREATE = true;
	
	// Don't forget to mkdir "~/.iris/lucene_idx" and update below.
	private static String indexDir = "/home/alexandrelucchesi/.iris/lucene_idx/";
	
	public static void main(String[] args) throws DBException, IOException {
		if (CREATE) {
			IndexManager.createIndex(indexDir);
		} else {
			IndexManager.setIndex(indexDir); // Set index directory.
		}
		
		EmailDAO emailDAO = new EmailDAO();
		emailDAO.saveMessage(message);
		
		IndexManager.closeIndex();
	}

}
