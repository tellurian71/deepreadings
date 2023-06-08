package com.deepreadings.service;

import java.security.Principal;
import java.util.List;

import com.deepreadings.model.Annotation;

public interface AnnotationService {
	void create(Annotation newAnnotation, Principal principal);
	Annotation readIfAuthorized(long annotationId, Principal principal);
	List<Annotation> readAllAuthorized(int documentId, Principal principal);
	Annotation updateIfOwner(Annotation detachedAnnotation, Principal principal);
	void deleteIfOwner(Annotation annotation, Principal principal);	
}
