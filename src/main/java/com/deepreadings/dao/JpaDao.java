package com.deepreadings.dao;

import java.util.List;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.NonUniqueResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class JpaDao<T, ID> {

	private static final Logger logger = LoggerFactory.getLogger(JpaDao.class);

	private Class<T> clazz;

	@PersistenceContext
	EntityManager entityManager;

	public final void setClazz(final Class<T> clazzToSet) {
		this.clazz = clazzToSet;
	}
	
	public T read(ID id) {
		return entityManager.find(clazz, id);
	}
	
	public T readByName(String name) {		
		T returnValue = null;
		try {
			returnValue = entityManager
					.createQuery("FROM " + clazz.getName() + " t WHERE t.name = :paramValue", clazz)
					.setParameter("paramValue", name)
		    		.getSingleResult();
		} catch(NoResultException noresult) {
		    // if there is no result
			logger.info("name: {}  not found!", name);
		} catch(NonUniqueResultException notUnique) {
		    // if more than one result
			logger.info("name: {} is not unique! Query returned multiple rows.", name);			
		}
		return returnValue;		
	}
	
	public <P> T readByProperty(String propertyName, final P propertyValue) {		
		T returnValue = null;
		try {
			returnValue = entityManager
					.createQuery("FROM " + clazz.getName() + " t WHERE t." + propertyName + " = :paramValue", clazz)
					.setParameter("paramValue", propertyValue)
		    		.getSingleResult();
		} catch(NoResultException noresult) {
		    // if there is no result
		logger.info("propertyName - propertyValue:{} - {}  not found!", propertyName, propertyValue);
		} catch(NonUniqueResultException notUnique) {
		    // if more than one result
			logger.info("propertyValue: {} is not unique! Query returned multiple rows.", propertyValue);			
		}
		return returnValue;		
	}
	
	@SuppressWarnings("unchecked")
	public List<T> readAll() {
		Query query = entityManager.createQuery("FROM " + clazz.getName());
		return query.getResultList();
	}

	public void create(T entity) {
		logger.info(entity.toString());
		entityManager.persist(entity);
	}

	public void update(T entity) {
		entityManager.merge(entity);
	}

	public void delete(T entity) {
		entityManager.remove(entity);
	}

}