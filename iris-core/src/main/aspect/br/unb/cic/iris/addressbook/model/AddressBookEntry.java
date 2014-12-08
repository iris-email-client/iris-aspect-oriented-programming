package br.unb.cic.iris.addressbook.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * A class that represents an address book entry. 
 * 
 * @author modularidade
 */
@Entity
@Table(name="TB_ADDRESS_BOOK")
public class AddressBookEntry {

	@Id
	//@GeneratedValue
	private String id;
	
	@Column(unique=true)
	private String nick;
	
	@Column
	private String address;
	
	public AddressBookEntry() {}
	
	public AddressBookEntry(String nick, String address) {
		this.nick = nick;
		this.address = address;
	}

	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
}
