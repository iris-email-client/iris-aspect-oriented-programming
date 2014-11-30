package br.unb.cic.iris.core.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

@Entity
@Table(name = "TB_TAG")
public class Tag {
	
	@Id
	private String id;
	
	@Column(columnDefinition = "TEXT", nullable = false, unique = true)
	private String name;
	
	@Cascade({CascadeType.DETACH})
	@JoinTable(name = "TB_TAG_MESSAGE")
	@ManyToMany(fetch = FetchType.LAZY)
	private Set<EmailMessage> messages;
	
	public Tag() {
		this(null);
	}
	
	public Tag(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<EmailMessage> getMessages() {
		if (messages == null) {
			messages = new HashSet<EmailMessage>();
		}
		return messages;
	}

	public void setMessages(Set<EmailMessage> messages) {
		this.messages = messages;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Tag other = (Tag) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

}
