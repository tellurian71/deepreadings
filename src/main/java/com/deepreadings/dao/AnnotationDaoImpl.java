package com.deepreadings.dao;

import java.util.List;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.deepreadings.model.Annotation;
import com.deepreadings.model.Concept;
import com.deepreadings.model.Reader;
import com.deepreadings.service.ConceptService;
import com.deepreadings.service.DocumentService;


@Repository
public class AnnotationDaoImpl extends JpaDao<Annotation, Long> implements AnnotationDao {

	private static final Logger logger = LoggerFactory.getLogger(AnnotationDaoImpl.class);
	
	@PersistenceContext
	EntityManager entityManager;
	
	@Autowired
	DocumentService documentService;

//	@Autowired
//	ConceptService conceptService;
	
	public AnnotationDaoImpl() {
		this.setClazz(Annotation.class);				
	}

	@Override
	public void create(Annotation newAnnotation) {
//		try {
//			newAnnotation.getConcepts().forEach(ac-> {
//				conceptService.create(ac.getConcept(), null);
//				//entityManager.persist(ac.getConcept());
//			});
			entityManager.persist(newAnnotation);
//		} catch (Exception e) {
//			logger.error(e.getMessage());
//		}
	}
	
	
	
	@Override
	public List<Annotation> readAllFiltered(int documentId, Reader reader) {
		
//	@SuppressWarnings("unchecked")
//	List<Annotation> annotations = entityManager.createNativeQuery(
//		" SELECT A.* " +
//		" FROM 	APP.ANNOTATIONS A, APP.USERS U, APP.USERS U2, APP.USER_GRANTS UG" +
//		" WHERE A.DOCUMENT_ID=:documentId" +
//		" AND A.USER_ID = U.ID " +
//		" AND U.ID = UG.GRANTOR_ID" +
//		" AND U2.ID = UG.GRANTEE_ID " +
//		" AND U2.ID = :userId" +
//				" UNION ALL " +
//		" SELECT A.*" +
//		" FROM 	APP.ANNOTATIONS A, APP.USERS U" +
//		" WHERE A.DOCUMENT_ID=:documentId" +
//		" AND A.USER_ID = U.ID " +
//		" AND U.ID = :userId", Annotation.class)
//			.setParameter("userId", user.getId())
//			.setParameter("documentId", documentId)
//			.getResultList(); 		
		
		List<Annotation> annotations = entityManager.createQuery(
			"FROM Annotation a " +
			"WHERE a.document.id = :docId " +
			"AND (a.user.id IN " +
				"(SELECT ug.grantor.id FROM ReaderGrant ug " + 
					"WHERE ug.grantee.id IN (:userId, 'ALL') ) OR a.user.id = :userId)", 
	 		Annotation.class)
				.setParameter("docId", documentId)
				.setParameter("userId", reader.getId())
				.getResultList();
		return annotations;
	}

	
	@Override
	public List<Annotation> readAll(int documentId) {
		List<Annotation> result = entityManager.createQuery(
				"FROM Annotation WHERE document.id = :docId", Annotation.class)
				.setParameter("docId", documentId)
				.getResultList();
		return result;
	}
	
	
//	@Override
//	public void create(Map<String, String> aDto) {
//		Annotation newAnnotation = new Annotation();
//		newAnnotation.setDocument(documentService.read(Integer.parseInt(aDto.get("documentId"))));
//		newAnnotation.setStartNodeId(aDto.get("startNodeId"));
//		newAnnotation.setStartOffset(aDto.get("startOffset"));
//		newAnnotation.setEndNodeId(aDto.get("endNodeId"));
//		newAnnotation.setEndOffset(aDto.get("endOffset"));
//		newAnnotation.setComment(aDto.get("comment"));
//		newAnnotation.setConcept(conceptService.read(Integer.parseInt(aDto.get("conceptId"))));
//		entityManager.persist(newAnnotation);
//		logger.info("newAnnotation: {}", newAnnotation);
//	}

//	@Override
//	public Annotation read(long annotationId) {
//		return entityManager.find(Annotation.class, annotationId);
//	}

//	@Override
//	public Annotation update(Annotation annotation) {
//		Annotation managedAnnotation = entityManager.merge(annotation);
//		return managedAnnotation;
//	}
		
//	@Override
//	public boolean delete(long annotationId) {
//		try {
//			Annotation annotation = read(annotationId);
//			logger.info("before remove: {}", annotation );
//			entityManager.remove(annotation);
//		} catch (Exception e) {
//			return false;
//		}
//		return true;
//	}

//	@Override
//	public boolean delete(Annotation annotation) {
//		try {
//			entityManager.remove(annotation);
//		} catch (Exception e) {
//			return false;
//		}
//		return true;
//	}

}

