package br.unb.cic.iris.addressbook.persistence;

import java.util.List;

import br.unb.cic.iris.addressbook.model.AddressBookEntry;
import br.unb.cic.iris.core.exception.DBException;

/**
 * A DAO for address books. 
 * 
 * @author modularidade
 *
 */
public interface IAddressBookDAO {

	public void save(AddressBookEntry entry) throws DBException;
	
	public AddressBookEntry find(String nick) throws DBException;
	
	public void delete(String nick) throws DBException;
	
	public List<AddressBookEntry> findAll() throws DBException;
}
