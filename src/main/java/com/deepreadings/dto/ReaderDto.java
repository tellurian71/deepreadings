package com.deepreadings.dto;

import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@Entity
public class ReaderDto {

	private static final Logger logger = LoggerFactory.getLogger(ReaderDto.class);	
	
	@Id
	private int id;	
	
	private String name;	
	
	private String email;	
	
	private String grantPolicy = "SELECTION";
	
	public ReaderDto(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public ReaderDto() {}
	
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getGrantPolicy() {
		return grantPolicy;
	}

	public void setGrantPolicy(String grantPolicy) {
		this.grantPolicy = grantPolicy;
	}
	
	@Override
	public boolean equals(Object o) {
		if ( this == o ) {
			return true;
		}
		if ( o == null || getClass() != o.getClass() ) {
			return false;
		}

		ReaderDto that = (ReaderDto) o;
		return Objects.equals( this.getId(), that.getId() ) &&
				Objects.equals( this.getName(), that.getName());
	}

	
	@Override
	public int hashCode() {
		return Objects.hash(id, name);
	}	
	
}
