package br.unb.cic.iris.persistence.lucene;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import br.unb.cic.iris.core.exception.DBException;
import br.unb.cic.iris.core.model.EmailMessage;
import br.unb.cic.iris.core.model.IrisFolder;

public class TestLucene {

	// Don't forget to mkdir "~/.iris/lucene_idx" and update below.
	private static String DEFAULT_IDX_DIR = "/home/alexandrelucchesi/.iris/lucene_idx/";

	public static void main(String[] args) throws DBException, IOException,
			ParseException {
		//setUp(DEFAULT_IDX_DIR, true);
		search(DEFAULT_IDX_DIR);
	}

	// -------------------------------------------------------
	// SETUP
	// -------------------------------------------------------

	static EmailMessage msg1, msg2;

	static {
		msg1 = new EmailMessage();
		msg1.setFrom("alexandrelucchesi@gmail.com");
		msg1.setTo("rbonifacio123@gmail.com");
		msg1.setCc("jeremiasmg@gmail.com");
		msg1.setBcc("somebcc@gmail.com");
		msg1.setSubject("Some subject");
		msg1.setMessage("Testing Lucene. :-)");
		msg1.setDate(new Date());
		msg1.setFolder(new IrisFolder(19, "UnB"));

		msg2 = new EmailMessage();
		msg2.setFrom("jeremiasmg@gmail.com");
		msg2.setTo("alexandrelucchesi@gmail.com");
		msg2.setCc("");
		msg2.setBcc("");
		msg2.setSubject("Deal with it");
		msg2.setMessage("I would NEVA put my name on it!");
		Date date = null;
		try {
			date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2014-12-25 06:30:21");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		msg2.setDate(date);
		msg2.setFolder(new IrisFolder(19, "UnB"));
	}

	private static void setUp(String indexDir, boolean create)
			throws DBException, IOException, ParseException {
		if (create) {
			IndexManager.createIndex(indexDir);
		} else {
			IndexManager.setIndex(indexDir); // Set index directory.
		}

		EmailDAO emailDAO = new EmailDAO();

		emailDAO.saveMessage(msg1);
		emailDAO.saveMessage(msg2);

		IndexManager.closeIndex();
		
		System.out.println("Success! :-)");
	}

	// -------------------------------------------------------
	// SEARCH
	// -------------------------------------------------------
	private static void search(String indexDir) throws DBException, IOException {
		IndexManager.setIndex(indexDir); // Set index directory.

		EmailDAO emailDAO = new EmailDAO();

		Date lastDate = emailDAO.lastMessageReceived();
		System.out.println(lastDate);

		IndexManager.closeIndex();
	}

}
