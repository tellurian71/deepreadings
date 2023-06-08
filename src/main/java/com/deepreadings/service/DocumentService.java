package com.deepreadings.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.deepreadings.model.Document;

public interface DocumentService {
	
	List<Document> readAll();
	void create(Document newDocument, MultipartFile mpFile) throws IOException;
	Document read(int documentId);
	void update(Document document, String multipartFileName) throws IOException;
	void delete(int documentId);
	void unpackDocument(int documentId, String targetFolder) throws IOException;
	boolean isDocumentUnpacked(int documentId, String targetFolder);
}
