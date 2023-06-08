package com.deepreadings.dao;

import java.util.List;

import com.deepreadings.model.Document;

public interface DocumentDao {
	List<Document> readAll();
	void create(Document newDocument);
	Document read(Integer documentId);
	void update(Document document);
	void delete(Document document);
}
