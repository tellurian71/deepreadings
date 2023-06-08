package com.deepreadings.controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.deepreadings.model.Annotation;
import com.deepreadings.service.AnnotationService;
import com.deepreadings.service.ConceptService;
import com.deepreadings.service.ReaderService;

@RestController
@RequestMapping("rest/annotations")

public class AnnotationRestController {

	private static final Logger logger = LoggerFactory.getLogger(AnnotationRestController.class);
	
	@Autowired
	AnnotationService annotationService;	
	
	@Autowired
	ConceptService conceptService;
	
	@Autowired
	ReaderService readerService;	

	@RequestMapping( value={"/csrfToken"}, method=RequestMethod.GET)
	@ResponseBody
	public String getCsrfToken(CsrfToken token) {		    
	    return token.getToken();
	}	
	
	
	@RequestMapping(value="/{documentId}", method=RequestMethod.GET)
	@ResponseBody
	public List<Annotation> readAll(@PathVariable(value="documentId") int documentId, Principal principal) {
		List<Annotation> annotationList = annotationService.readAllAuthorized(documentId, principal);
		logger.info("##### AnnotationList: {}", annotationList);
		if (principal!=null)	logger.info("##### Principal: {}", principal.getName());
		return annotationList;
	}



	@RequestMapping(value="/create", method=RequestMethod.POST)
	@ResponseBody
	public Annotation create(
			@RequestBody Annotation newAnnotation, 
			@RequestHeader Map<String, String> headers,
			Principal principal) {
		
		headers.forEach((key, value) -> logger.info("#####{}: {}", key,value));
		logger.info("The principal.getName() is: {}", principal.getName());
		logger.info("newAnnotation: {}", newAnnotation);
		annotationService.create(newAnnotation, principal);
		return newAnnotation;
	}
	
	
	
	@RequestMapping(value="/update", method=RequestMethod.PUT)
	@ResponseBody
	public Annotation update(@RequestBody Annotation detachedAnnotation, Principal principal) {
		logger.info("=====================updatedDetachedAnnotation: {}", detachedAnnotation);
		Annotation managedAnnotation = annotationService.updateIfOwner(detachedAnnotation, principal);
		return managedAnnotation;
	}	

	
	
	@RequestMapping(value="/delete/{annotationId}", method=RequestMethod.DELETE)
	@ResponseBody
	public void delete(@PathVariable(value="annotationId") int annotationId, Principal principal) {
		Annotation annotationToDelete = annotationService.readIfAuthorized(annotationId, principal);
		annotationService.deleteIfOwner(annotationToDelete, principal); 	
	}
	
}
