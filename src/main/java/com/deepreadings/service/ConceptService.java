package com.deepreadings.service;

import java.security.Principal;
import java.util.List;

import com.deepreadings.model.Concept;

public interface ConceptService {
	
	public List<Concept> readAll();
	public Concept read(int conceptId);
	public Concept readByName(String conceptName);
	public void create(Concept newConcept, Principal principal);
	public void update(Concept concept, Principal principal);
	public void delete(Concept concept);
}
