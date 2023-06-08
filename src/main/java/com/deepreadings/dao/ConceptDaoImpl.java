package com.deepreadings.dao;

import org.springframework.stereotype.Repository;

import com.deepreadings.model.Concept;

@Repository
public class ConceptDaoImpl extends JpaDao<Concept, Integer> implements ConceptDao {
	
	public ConceptDaoImpl() {
		this.setClazz(Concept.class);		
	}	

}
