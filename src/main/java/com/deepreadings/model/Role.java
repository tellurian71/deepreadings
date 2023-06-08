package com.deepreadings.model;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;


@Entity
@Table(name="ROLES",
	uniqueConstraints = {
			@UniqueConstraint(
					columnNames = {"name"},
					name="ROLE_NAME_UK")}
)
public class Role {

	
	@Id
	@Column(name="ID")
	private int id;	
	
	@Column(name="NAME")
	private String name;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object o) {
		if ( this == o ) {
			return true;
		}
		if ( o == null || getClass() != o.getClass() ) {
			return false;
		}

		Role that = (Role) o;
		return Objects.equals( id, that.getId() ) &&
				Objects.equals( name, that.getName() );
	}

	
	@Override
	public int hashCode() {
		return Objects.hash(id, name);
	}	
	
	
}
