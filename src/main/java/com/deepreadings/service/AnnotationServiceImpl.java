package com.deepreadings.service;

import java.security.Principal;
import java.time.Instant;
import java.util.List;
import jakarta.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.deepreadings.dao.AnnotationDao;
import com.deepreadings.model.Annotation;
import com.deepreadings.model.AnnotationConcept;
import com.deepreadings.model.Concept;
import com.deepreadings.model.Document;
import com.deepreadings.model.Reader;

@Service
@Transactional
public class AnnotationServiceImpl implements AnnotationService {
	
	private static final Logger logger = LoggerFactory.getLogger(AnnotationServiceImpl.class);

	@Autowired
	AnnotationDao annotationDao;
 
	@Autowired
	ReaderService readerService;
	
	@Autowired
	DocumentService documentService;	

	@Autowired
	ConceptService conceptService;

   
	@Override
	public void create(Annotation newAnnotation, Principal principal) {	
		Document managedDocument = documentService.read(newAnnotation.getDocument().getId());
		Reader reader = readerService.readByName(principal.getName());
		newAnnotation.setDocument(managedDocument);	
		newAnnotation.setTimestamp(Instant.now());
		newAnnotation.setOwner(reader);
		newAnnotation.getConcepts().forEach(ac-> {
			conceptService.create(ac.getConcept(), principal);
		});
		annotationDao.create(newAnnotation);		
	}

	
    @Override
	public Annotation readIfAuthorized(long annotationId, Principal principal) {
    	Reader requestor = readerService.readByName(principal.getName());
    	Annotation managedAnnotation = annotationDao.read(annotationId);
    	logger.info("managedAnnotation: {}", managedAnnotation);
    	logger.info("principal: {}", principal);
    	logger.info("requestor obtained from principal: {}", requestor);
    	if (managedAnnotation.getOwner().isReadAccessGrantedTo(requestor)) {
			//trigger lazy fetches of Concept...
			managedAnnotation.getConcepts().forEach(ac -> {
				ac.getConcept().getUpperConcepts().size();
				ac.getConcept().getLowerConcepts().size();
    			ac.getConcept().getAnnotations().size();
			});
			return managedAnnotation;	
		}
		return null;
	}


	@Override
	public List<Annotation> readAllAuthorized(int documentId, Principal principal) {
    	Reader reader = readerService.readByName(principal.getName());
	 	
    	//performance shall be the decisive factor to choose which strategy is better. DB filtering or Service level filtering..
    	//return annotationDao.readAllFiltered(documentId, user);
    	
    	List<Annotation> filteredAnnotations = annotationDao.readAll(documentId);
    	filteredAnnotations.removeIf(a -> !a.getOwner().isReadAccessGrantedTo(reader));

    	//trigger lazy fetches...
    	filteredAnnotations.forEach(annotation -> {
    		annotation.getConcepts().forEach(ac -> {
    			ac.getConcept().getUpperConcepts().size();
    			ac.getConcept().getLowerConcepts().size();
    			ac.getConcept().getAnnotations().size();
    		});   		
    	});

		return filteredAnnotations;
	} 
	
	
	@Override
	public Annotation updateIfOwner(Annotation detachedAnnotation, Principal principal) {

		AnnotationConcept annotationConcept = null;
		Annotation managedAnnotation = readIfAuthorized(detachedAnnotation.getId(), principal);
		logger.info("#####managedAnnotation {}", managedAnnotation);
		if (!(managedAnnotation.getOwner().getName().equals(principal.getName()))) {
			logger.info("##############Attempt to update unauthorized object. Annotation-{}", managedAnnotation.getId());
			return managedAnnotation;
		}
	
		managedAnnotation.setType(detachedAnnotation.getType());
		managedAnnotation.setComment(detachedAnnotation.getComment());
		managedAnnotation.setTimestamp(Instant.now());
		
		//concepts missing in the detachedAnnotation shall be removed from the managedAnnotation
		for(int i = managedAnnotation.getConcepts().size()-1; i>=0; i--) {
			annotationConcept = managedAnnotation.getConcepts().get(i);
			if (!(detachedAnnotation.getConceptNames().contains(annotationConcept.getConcept().getName()))) {
				managedAnnotation.removeConcept(annotationConcept.getConcept());				
			}
		}
		
		//add new concepts of the detachedAnnotation to the managedAnnotation.
		for(int i = detachedAnnotation.getConcepts().size()-1; i>=0; i--) {
			annotationConcept = detachedAnnotation.getConcepts().get(i);
			if (!(managedAnnotation.getConceptNames().contains(annotationConcept.getConcept().getName()))) {
				//according to the example in hibernate documentation about  
				//Bidirectional ManyToMany Association with A Link Entity
				//first both end entities are persisted and than the entities are
				//associated. 
				Concept newConcept = conceptService.readByName(annotationConcept.getConcept().getName());
				logger.info("#####adding an existing concept to annotation: {}", newConcept);
				if (newConcept == null) {
					newConcept = annotationConcept.getConcept();
					detachedAnnotation.removeConcept(newConcept);
					conceptService.create(newConcept, principal);
					logger.info("#####adding a really new Concept to annotation: {}", newConcept);
				}
				managedAnnotation.addConcept(newConcept);
			}
		}		
		return managedAnnotation;	
	}

	
	@Override
	public void deleteIfOwner(Annotation annotation, Principal principal) {
		if (annotation.getOwner().getName().equals(principal.getName())) {
			logger.info("deleting: {}           principal: {}" , annotation.getId(), principal);
			annotationDao.delete(annotation);	
		}
	}


}






