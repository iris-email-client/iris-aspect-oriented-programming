package br.unb.cic.iris.persistence.lucene;

import br.unb.cic.iris.persistence.IAddressBookDAO;
public aspect FeatureAddressBookDAO {

		public interface IAddressBookDAOFactory {
			public IAddressBookDAO createAddressBookDAO();
		}
		
		public IAddressBookDAO IAddressBookDAOFactory.createAddressBookDAO() {
			return AddressBookDAO.instance();
		}
		
		declare parents:  br.unb.cic.iris.persistence.DAOFactory implements IAddressBookDAOFactory;
}
