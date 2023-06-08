package com.deepreadings.dao;

import java.util.List;

import com.deepreadings.model.Concept;

public interface ConceptDao {

	List<Concept> readAll();
	Concept read(Integer conceptId);
	Concept readByName(String conceptName);
	void create(Concept concept);
	void update(Concept concept);
	void delete(Concept concept);
	
}
