package com.deepreadings.service;

import java.security.Principal;
import java.time.Instant;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.deepreadings.dao.ConceptDao;
import com.deepreadings.model.Concept;
import com.deepreadings.model.ConceptHierarchy;
import com.deepreadings.model.Reader;

import jakarta.transaction.Transactional;


@Service
@Transactional
public class ConceptServiceImpl implements ConceptService {

	@Autowired
	ConceptDao conceptDao;
		 
	@Autowired
	ReaderService readerService;
	
		
	private static final Logger logger = LoggerFactory.getLogger(ConceptServiceImpl.class);

	public List<Concept> readAll() {
		List<Concept> concepts = conceptDao.readAll();

		//trigger the lazy fetching of child associations
		for(Concept c: concepts) {
			c.getUpperConcepts().size();
			c.getLowerConcepts().size();
			c.getAnnotations().size();
		}		
		return concepts;
	}
	

	@Override
	public Concept read(int conceptId) {
		Concept concept = conceptDao.read(conceptId);

		//trigger the lazy fetching of child associations
		concept.getUpperConcepts().size();
		concept.getLowerConcepts().size();
		concept.getAnnotations().size();
		return concept;
	}	

	@Override
	public Concept readByName(String conceptName) {
		Concept concept = conceptDao.readByName(conceptName);
		if (concept != null) {
			//trigger the lazy fetching of child associations
			concept.getUpperConcepts().size();
			concept.getLowerConcepts().size();
			concept.getAnnotations().size();
		}
		return concept;
	}
	

	@Override
	public void create(Concept newConcept, Principal principal) {	
		Reader reader = readerService.readByName(principal.getName());
		newConcept.setCreatedBy(reader);	
		newConcept.setDateCreated(Instant.now());
		setHierarchies(newConcept);
		conceptDao.create(newConcept);
	} 
	
	
	private void setHierarchies(Concept newConcept) {
		Concept deficitConcept = null;	
		ConceptHierarchy hierarchy = null;
		
		for(int i = newConcept.getUpperConcepts().size()-1; i>=0; i--) {
			deficitConcept = newConcept.getUpperConcepts().get(i).getUpperConcept();
			Concept fullConcept = read(deficitConcept.getId());
			if (fullConcept!=null) {
				hierarchy = newConcept.getUpperConcepts().get(i);
				hierarchy.setUpperConcept(null);
				hierarchy.setLowerConcept(null);
				
				//use the list.remove() method instead of concept.remove() 
				//concept.remove() tests for equivalence of objects based on
				//id-name pair and consequently fails.
				newConcept.getUpperConcepts().remove(i);
				newConcept.addUpperConcept(fullConcept);
			}
		}

		for(int i = newConcept.getLowerConcepts().size()-1; i>=0; i--) {
			deficitConcept = newConcept.getLowerConcepts().get(i).getLowerConcept();
			Concept fullConcept = read(deficitConcept.getId());
			if (fullConcept!=null) {
				hierarchy = newConcept.getLowerConcepts().get(i);
				hierarchy.setUpperConcept(null);
				hierarchy.setLowerConcept(null);
				
				//use the list.remove() method instead of concept.remove() 
				//concept.remove() tests for equivalence of objects based on
				//id-name pair and consequently fails.
				newConcept.getLowerConcepts().remove(i);
				newConcept.addLowerConcept(fullConcept);
			}
		}
	}
	

	@Override
	public void update(Concept detachedConcept, Principal principal) {		
		logger.info("detachedConcept: {}", detachedConcept);
		Concept managedConcept = this.read(detachedConcept.getId());
		removeMarkedHierarchies(detachedConcept);
		syncManagedConcept(detachedConcept, managedConcept);
		Reader reader = readerService.readByName(principal.getName());
		managedConcept.setUpdatedBy(reader);
		managedConcept.setDateUpdated(Instant.now());		
		logger.info("managedConcept: {}", managedConcept);					
	}
	
	private void removeMarkedHierarchies(Concept concept) {	
		ConceptHierarchy hierarchy = null;
		for (int i = concept.getUpperConcepts().size()-1; i>=0; i--) {
			hierarchy = concept.getUpperConcepts().get(i);
			if (hierarchy.isMarkedForRemoval()) {
				concept.getUpperConcepts().remove(i);
			}
		}
		for (int i = concept.getLowerConcepts().size()-1; i>=0; i--) {
			hierarchy = concept.getLowerConcepts().get(i);
			if (hierarchy.isMarkedForRemoval()) {
				concept.getLowerConcepts().remove(i);
			}
		}		
	}		

	private void syncManagedConcept(Concept detached, Concept managed) {

		managed.setName(detached.getName());
		managed.setDescription(detached.getDescription());
		managed.setUpdatedBy(detached.getUpdatedBy());
		managed.setDateUpdated(detached.getDateUpdated());
		
		for(int i = managed.getUpperConcepts().size()-1; i>=0; i--) {
			Concept upperConcept = managed.getUpperConcepts().get(i).getUpperConcept();
			if (!detached.hasUpperConcept(upperConcept.getId())) {
				managed.removeUpperConcept(upperConcept);
			}
		}
		
		for(int i = detached.getUpperConcepts().size()-1; i>=0; i--) {
			Concept upperConcept = detached.getUpperConcepts().get(i).getUpperConcept();
			if (!managed.hasUpperConcept(upperConcept.getId())) {
				upperConcept = this.read(upperConcept.getId());
				managed.addUpperConcept(upperConcept);
			}
		}

		for(int i = managed.getLowerConcepts().size()-1; i>=0; i--) {
			Concept lowerConcept = managed.getLowerConcepts().get(i).getLowerConcept();
			if (!detached.hasLowerConcept(lowerConcept.getId())) {
				managed.removeLowerConcept(lowerConcept);
			}
		}
		
		for(int i = detached.getLowerConcepts().size()-1; i>=0; i--) {
			Concept lowerConcept = detached.getLowerConcepts().get(i).getLowerConcept();
			if (!managed.hasLowerConcept(lowerConcept.getId())) {
				lowerConcept = this.read(lowerConcept.getId());
				managed.addLowerConcept(lowerConcept);
			}
		}	
		
	}
	
	
	@Override
	public void delete(Concept concept) {
		conceptDao.delete(concept);		
	}

}
