package br.unb.cic.iris.addressbook.persistence;

import java.util.logging.Logger;

import br.unb.cic.iris.persistence.InjectDAOFactory;

public privileged aspect FeatureAddressBookDAO {

	private static final String BASE_PACKAGE = "br.unb.cic.iris.addressbook.persistence";
	private static final String CLASS =  "AddressBookDAO";
	
		public interface IAddressBookDAOFactory {
			public IAddressBookDAO createAddressBookDAO();
		}
		
		public IAddressBookDAO IAddressBookDAOFactory.createAddressBookDAO() {
			try {
				String name = BASE_PACKAGE + "." + InjectDAOFactory.persistenceType() + "." + CLASS;
				Class factory = Class.forName(name);
				return (IAddressBookDAO)factory.getMethod("instance").invoke(null);
			}
			catch(Exception e) {
				Logger.getLogger(InjectDAOFactory.class.getName()).severe("Could not instantiate DAOFactory");
				e.printStackTrace();
				throw new RuntimeException();
			}
		}
		
		declare parents:  br.unb.cic.iris.persistence.IDAOFactory implements IAddressBookDAOFactory;
}
