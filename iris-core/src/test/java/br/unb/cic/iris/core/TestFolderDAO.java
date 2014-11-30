package br.unb.cic.iris.core;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.unb.cic.iris.core.SystemFacade;
import br.unb.cic.iris.core.model.IrisFolder;
import br.unb.cic.iris.persistence.IFolderDAO;

public class TestFolderDAO {

	private static final String DEFAULT_NAME = "test-folder";
	private IFolderDAO dao;
	
	@Before
	public void setUp() throws Exception {
		try {
			dao = SystemFacade.instance().getDaoFactory().createFolderDAO();
		}
		catch(Exception e) {
			throw new Exception("could not setUp the tests", e);
		}
	}
	
	@Test
	public void save() {
		try {
			IrisFolder folder = new IrisFolder(DEFAULT_NAME);
			IrisFolder foundFolder = dao.findByName(DEFAULT_NAME);
			//TODO: Implement folder deletion and add and delete every time!
			if (foundFolder == null)
				dao.saveOrUpdate(folder);
			
			List<IrisFolder> all = dao.findAll();
			for(IrisFolder f: all){
				System.out.println("FOLDER: "+f.getName());
			}
			Assert.assertTrue(!all.isEmpty());
			
			//Clean up
			//dao.delete(folder);
		}
		catch(Exception e) {
			e.printStackTrace();
			Assert.fail("error while testing TestFolderDAO.save()");
		}
	}
	
}
