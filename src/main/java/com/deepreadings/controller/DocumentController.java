package com.deepreadings.controller;

import java.io.IOException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.deepreadings.model.Document;
import com.deepreadings.service.DocumentService;

@Controller
@MultipartConfig
@RequestMapping("/documents")
public class DocumentController {

	private static final Logger logger = LoggerFactory.getLogger(DocumentController.class);
	
	@Autowired
	DocumentService documentService;	
	
	
	@RequestMapping(value="", method=RequestMethod.GET)
	public String documentLister(Model model) {	
		model.addAttribute("documents", documentService.readAll() );
		return "documents";
	}
	
	

	@RequestMapping(value = "/edit/{documentId}", method = RequestMethod.GET)	
	public String documentEditorGET(Model model, @PathVariable(value="documentId") int documentId) {
		Document document = null;
		if (documentId == 0) {
			document = new Document();
		} else {
			document = documentService.read(documentId);
		}
		model.addAttribute("documentToEdit", document);
		return "documentEditor";
	}
	

	
	@RequestMapping(value = "/edit/{documentId}",	method = RequestMethod.POST)
	public String documentEditorPOST(
			@RequestParam("fileInput") MultipartFile multipartFile,
			@PathVariable("documentId") int documentId,
			@ModelAttribute("documentToEdit") Document returnedDocument, 
			Model model) throws IOException { 
		
		if (documentId == 0) { // create new Document
			if (!multipartFile.isEmpty()) {
				documentService.create(returnedDocument, multipartFile);
			} 
		} else { // update an existing Document
			if (!multipartFile.isEmpty()) {
				returnedDocument.setFileData(multipartFile.getBytes());
			} else {
				returnedDocument.setFileData(documentService.read(documentId).getFileData());			
			}
			documentService.update(returnedDocument, multipartFile.getOriginalFilename());
		}

		return "redirect:/documents";
	}	
	
	
	
	
	@RequestMapping(value="/delete/{documentId}", method=RequestMethod.POST)
	public String documentDeleter(@PathVariable(value="documentId") int documentId) {
		documentService.delete(documentId);
		return "redirect:/documents";
	}
	
//	@RequestMapping(value="/documents/view/{documentId}/{resourceName}", method=RequestMethod.GET)
//	public String getResource(
//			@PathVariable(value="documentId") int documentId,
//			@PathVariable(value="resourceName") String resourceName,
//			HttpServletRequest request, 
//			Model model) throws IOException {
//		
//		String forwardUrl = "/resources/ebooks/" + documentId + "/" + resourceName;
//		
//		logger.info("forwardUrl:{}", forwardUrl);
//		return "forward:" + forwardUrl;
//
//	}		

	
	
//	@RequestMapping(value="/documents/view/**", method=RequestMethod.GET)
//	public String documentViewer(
//			HttpServletRequest request, 
//			Model model) {
//
//		String relativeUrl = request.getServletPath();
//		logger.info("relativeUrl: {}", relativeUrl);
//		String[] urlParts = relativeUrl.split("/");
//		int documentId = 0;
//		try {
//			documentId = Integer.parseInt(urlParts[3]);			
//		} catch(NumberFormatException e) {
//			logger.info("failed to determine documentId. relativeUrl:{}", relativeUrl);
//		}
//		
//		String ebookFolder = "/ebooks/";		
//		String forwardUrl = null;
//		if (relativeUrl.endsWith(documentId+".html")) { //main document...
//			String unpackRealFolder = request.getSession().getServletContext().getRealPath("/") + ebookFolder + documentId;
//			documentService.unpackDocument(documentId, unpackRealFolder);		
//			forwardUrl = ebookFolder + documentId + "/" + documentId + ".html";
//		} else { //resource file...
//			forwardUrl = relativeUrl.replaceFirst("/documents/view/", ebookFolder);
//		}
//		logger.info("forwardUrl: {}", forwardUrl);
//		return "forward:" + forwardUrl;
//	}		
	
	
	@RequestMapping(value="/view/{documentId}", method=RequestMethod.GET)
	public String documentViewer(
			HttpServletRequest request, 
			@PathVariable("documentId") int documentId,
		Model model) {

		String relativeUrl = request.getServletPath();
		logger.info("relativeUrl: {}", relativeUrl);
		
		String ebookFolder = "/ebooks/";		
		String unpackRealFolder = request.getSession().getServletContext().getRealPath("/") + ebookFolder + documentId;
		try {
			documentService.unpackDocument(documentId, unpackRealFolder);
		} catch (IOException e) {
			logger.error("Failed to unpack document. Id: {}", documentId);
			e.printStackTrace();
		}		
		
		String	redirectUrl = ebookFolder + documentId + "/" + documentId + ".html";
		logger.info("redirectUrl: {}", redirectUrl);
		return "redirect:" + redirectUrl;
	}	
	

}
