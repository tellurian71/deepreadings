package com.deepreadings.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import com.deepreadings.model.Concept;
import com.deepreadings.model.Role;

//@Repository
//public class RoleDaoImpl implements RoleDao {
//	
//	@PersistenceContext
//	EntityManager entityManager;
//
//	@Override
//	public Role readByName(String name) {
//		
//		TypedQuery<Role> query = entityManager.createQuery(
//				  "SELECT r FROM Role r WHERE r.name = :rolename" , Role.class);
//		Role role = query.setParameter("rolename", name).getSingleResult();	
//				
//		return role;
//	}
//
//}

@Repository
public class RoleDaoImpl extends JpaDao<Role, Integer> implements RoleDao {
	
	public RoleDaoImpl() {
		this.setClazz(Role.class);	
	}	

}
