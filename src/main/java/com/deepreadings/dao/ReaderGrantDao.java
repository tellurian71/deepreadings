package com.deepreadings.dao;

import java.util.List;

import com.deepreadings.model.ReaderGrant;

public interface ReaderGrantDao {
	void create(ReaderGrant newUserGrant);
	List<ReaderGrant> readAll();
	void delete(ReaderGrant readerGrant);
}
