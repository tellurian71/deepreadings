package com.deepreadings.dao;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Repository;

import com.deepreadings.model.Reader;



@Repository 
@Transactional
public class ReaderDaoImpl extends JpaDao<Reader, Integer> implements ReaderDao 
{
	
	public ReaderDaoImpl() {
		this.setClazz(Reader.class);
	}

}
