package com.deepreadings.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table	(
	name = "CATEGORIES", 
	uniqueConstraints = @UniqueConstraint(
			name = "UK_CATEGORY_NAME", 
			columnNames = {"NAME"}
	)
)
public class Category {	
	
	@Id
	@GeneratedValue	
	@Column(name="ID")
	private int id;	
	
	@Column(name="NAME", nullable=false)
	private String name;
	
}