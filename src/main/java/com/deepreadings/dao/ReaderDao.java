package com.deepreadings.dao;

import java.util.List;

import com.deepreadings.model.Reader;

public interface ReaderDao {
	
	List<Reader> readAll();
	Reader read(Integer readerId);
	Reader readByName(String readerName);
	void create(Reader reader);
	void update(Reader reader);

}
