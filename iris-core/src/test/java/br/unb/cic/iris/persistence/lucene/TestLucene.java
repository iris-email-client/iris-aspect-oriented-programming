package br.unb.cic.iris.persistence.lucene;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

import br.unb.cic.iris.core.exception.DBException;
import br.unb.cic.iris.core.model.IrisFolder;
import br.unb.cic.iris.persistence.IFolderDAO;

public abstract class TestLucene {
	
	// When set to 'true', creates the index in the filesystem in the path specified by 'DEFAULT_IDX_DIR'.
	// When 'false', creates the index in RAM.
	protected static boolean FS_IDX = false;
	
	// Don't forget to mkdir "~/.iris/lucene_idx" and update below.
	protected static String DEFAULT_IDX_DIR = System.getProperty("user.home") + "/.iris/lucene_test_idx/";
	
	@BeforeClass
	public static void clearFSDirectory() throws IOException {
		if (FS_IDX) {
			File dir = new File(DEFAULT_IDX_DIR);
			if (!dir.exists() || !dir.isDirectory())
				throw new IOException("Specified index directory does not exist. " + DEFAULT_IDX_DIR);
			
			// Clears the directory...
			for (File f : dir.listFiles())
				f.delete();
		}
	}
	
	@Before
	public void setUpIndex() throws IOException, DBException {
		if (FS_IDX)
			IndexManager.createIndex(DEFAULT_IDX_DIR);
		else
			IndexManager.createIndex(null);
		
		// Adds standard folders.
		IFolderDAO folderDAO = FolderDAO.instance();
		folderDAO.saveOrUpdate(new IrisFolder("INBOX"));
		folderDAO.saveOrUpdate(new IrisFolder("OUTBOX"));
	}
	
	
	@After
	public void tearDownIndex() throws IOException {
		IndexManager.closeIndex();
		clearFSDirectory();
	}
	
}
