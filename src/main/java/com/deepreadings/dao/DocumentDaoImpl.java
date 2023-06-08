package com.deepreadings.dao;

import org.springframework.stereotype.Repository;

import com.deepreadings.model.Document;

@Repository
public class DocumentDaoImpl extends JpaDao <Document, Integer> implements DocumentDao {
		
	public DocumentDaoImpl() {
		this.setClazz(Document.class);		
	}

}
