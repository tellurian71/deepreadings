package com.deepreadings.dao;

import java.util.List;

import com.deepreadings.model.Annotation;
import com.deepreadings.model.Reader;

public interface AnnotationDao {

	void create(Annotation newAnnotation);
	Annotation read(Long annotationId);
	List<Annotation> readAll(int documentId);
	List<Annotation> readAllFiltered(int documentId, Reader reader);
	void update(Annotation annotation);
	void delete(Annotation annotation);
}
