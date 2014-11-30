/*
 * Element
 * ---------------------------------
 *  version: 0.0.1
 *  date: Sep 6, 2014
 *  author: rbonifacio
 *  list of changes: (none) 
 */
package br.unb.cic.iris.core.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

/**
 * An abstract class that represents both folders and elements. Note that this class is empty, it is only useful to
 * provide an hierarchy that comprises both folders and elements.
 * 
 * In addition, it is not possible to instantiate an object of this class.
 * 
 * @author rbonifacio
 */
@Entity
@Table(name = "TB_FOLDER_CONTENT")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class FolderContent {

	public FolderContent() {
		this(null);
	}

	public FolderContent(String id) {
		super();
		this.id = id;
	}

	@Id
	//@GeneratedValue(strategy = GenerationType.TABLE)
	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		FolderContent other = (FolderContent) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
