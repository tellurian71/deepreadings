package com.deepreadings.dao;

import org.springframework.stereotype.Repository;

import com.deepreadings.model.ReaderGrant;

@Repository
public class ReaderGrantDaoImpl extends JpaDao<ReaderGrant, Integer> implements ReaderGrantDao {
		
	public ReaderGrantDaoImpl() {
		this.setClazz(ReaderGrant.class);		
	}

}
