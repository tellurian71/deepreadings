package com.deepreadings.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.deepreadings.dao.DocumentDao;
import com.deepreadings.model.Document;

@Service
public class DocumentServiceImpl implements DocumentService {
	
	private static final Logger logger = LoggerFactory.getLogger(DocumentServiceImpl.class);

	@Autowired
	DocumentDao documentDao;

	@Override
	public List<Document> readAll() {
		return documentDao.readAll();
	};

	
	@Override
	@Transactional
	public void create(Document document, MultipartFile mpFile) throws IOException {
		documentDao.create(document); // to get the persisted documentId for use in the following statements...

		String mpFileName = mpFile.getOriginalFilename();
		if (mpFileName.endsWith(".html") || mpFileName.endsWith(".htm")) {
			String originalHtml = new String(mpFile.getBytes(), StandardCharsets.UTF_8);
			String originalCharSet = determineOriginalCharSet(originalHtml);
			originalHtml = new String(mpFile.getBytes(), originalCharSet);
			logger.info("originalHtml parsed with originalCharSet: {}", originalHtml);
			String adaptedHtml = adaptHtml(originalHtml, originalCharSet);
			logger.info("adaptedHtml encoded in UTF-8: {}", adaptedHtml);
			
			byte[] adaptedZip = zip(adaptedHtml, document.getId() + ".html");
			document.setFileData(adaptedZip);
		} else if (mpFileName.endsWith(".zip")) {
			byte[] adaptedZip = adaptZip(mpFile.getBytes(), document.getId());
			document.setFileData(adaptedZip);
		}
	}
	
	
	@Override
	public Document read(int documentId) {
		return documentDao.read(documentId);
	}	
	

	@Override
	@Transactional
	public void update(Document document, String mpFileName) throws IOException {

		String originalHtml = null, adaptedHtml = null, originalCharSet = null;
		Document managedDocument = read(document.getId());
		logger.info("mpFileName: {}", mpFileName);
		
		int annotationCount = managedDocument.getAnnotations().size();
		if (annotationCount > 0 && !mpFileName.isBlank()) {
			throw new IOException("The existing document has " + annotationCount 
				+ " annotation(s) associated with it. Replacing the document shall break consistency. "
				+ "Unable to replace the document!");
		}
			
		if (mpFileName.endsWith(".html") || mpFileName.endsWith(".htm")) {
			originalHtml = new String(document.getFileData(), StandardCharsets.UTF_8);
			originalCharSet = determineOriginalCharSet(originalHtml);
			originalHtml = new String(document.getFileData(), originalCharSet);
			adaptedHtml = adaptHtml(originalHtml, originalCharSet);
			byte[] adaptedZip = zip(adaptedHtml, document.getId() + ".html");
			document.setFileData(adaptedZip);
		} else if (mpFileName.endsWith(".zip")) {
			byte[] adaptedZip = adaptZip(document.getFileData(), document.getId());
			document.setFileData(adaptedZip);
		} 
		
		documentDao.update(document);
	}

	@Override
	@Transactional 
	public void delete(int documentId) {
		Document document = documentDao.read(documentId);
		logger.info("annotationCount of Document-{}: {}", documentId, document.getAnnotations().size());
		if (document.getAnnotations().size() == 0) {
			documentDao.delete(document);
		}
	}	
	
	
	@Override
	public void unpackDocument(int documentId, String targetFolder) throws IOException {
		logger.info("targetFolder: {} ", targetFolder);
		Path targetPath = Path.of(targetFolder);
		if (Files.exists(targetPath)) { //document is already unpacked.
			logger.info("targetPath exists: {} ", targetPath);
		} else { //create parent folder hierarchy for the document.
			Files.createDirectories(Paths.get(targetFolder));
		}

		Document document = this.read(documentId);
		ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(document.getFileData()));
		ZipEntry zipEntry = zis.getNextEntry();
		while (zipEntry != null) {
			// some zips store files and folders separately
			// e.g data/
			//     data/folder/
       		//     data/folder/file.txt
   			// others store file path only, need create parent directories
   			// e.g data/folder/file.txt
			
        	Path newPath = zipSlipProtect(zipEntry.getName(), targetPath);
			//logger.info("zipEntry.getName():{}", zipEntry.getName());
			//logger.info("newPath:{}", newPath);
			if (zipEntry.getName().equals("")) { //first entry is blank in some zipfiles? just skip.
				logger.info("blank zip entry!!!"); //
			} else if (zipEntry.getName().endsWith(File.separator)) {  //entry is a directory
       			Files.createDirectories(newPath);
       		} else {
       			if (newPath.getParent() != null) {
       				Files.createDirectories(newPath.getParent());
       			}             
       			Files.copy(zis, newPath, StandardCopyOption.REPLACE_EXISTING);               
       		} 
       		zipEntry = zis.getNextEntry();             
       	}      
       	zis.closeEntry();        
	}



	
	// protect zip slip attack
	private static Path zipSlipProtect(String entryName, Path targetDir)
			throws IOException {

		// test zip slip vulnerability
		// Path targetDirResolved = targetDir.resolve("../../" + entryName);

		Path targetDirResolved = targetDir.resolve(entryName);

		// make sure normalized file still has targetDir as its prefix
		// else throws exception
		Path normalizedPath = targetDirResolved.normalize();
		if (!normalizedPath.startsWith(targetDir)) {
			logger.error("normalizePath: {}, targetDir: {}", normalizedPath, targetDir);
			throw new IOException("Bad zip entry: " + entryName);
		}
		return normalizedPath;
	}


	private static String adaptHtml(final String originalHtml, final String originalCharSet) throws IOException {
		
		org.jsoup.nodes.Document htmlDoc = Jsoup.parse(originalHtml);	
		int charSetCount = 0;

		//set charset encoding to UTF-8
		//if charset style is: <meta charset="8859-1">
		Elements charSetElements = htmlDoc.head().select("meta[charset]");
		if (charSetElements.size() == 1) {
			charSetCount++;
			charSetElements.get(0).attr("charset", "UTF-8");
		} 
	
		
		//if charset style is: <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		charSetElements = htmlDoc.head().select("meta[http-equiv=Content-Type]");
		if (charSetElements.size() ==  1) {
			charSetCount++;
			String attributeValue = charSetElements.get(0).attr("content");
			attributeValue = attributeValue.replaceAll(originalCharSet, "UTF-8");
			charSetElements.get(0).attr("content", attributeValue);
		}
		if (charSetCount != 1) {
			throw new IOException("Unable to determine charset");
		}

			
		//add id attribute to each single element that can possibly be annotated by a reader.
		Elements elements = htmlDoc.select("h1, h2, h3, h4, h5, h6, p, li, th, tr, img, pre, div");
		int drId = 0;
		for (Element e: elements) {
			//logger.info("e.tag(): {}", e.tag());
			if (!e.hasAttr("id")) {
				e.attr("id", "dr" + drId++);
			}
		}
		
		
		//add ref to the deepReader script.
		if (htmlDoc.getElementById("drScript")==null){
			Element drScriptRef = new Element(Tag.valueOf("script"), null);
			drScriptRef.attr("id", "drScript");			
			drScriptRef.attr("src", "/js/drAnnotationManager.js"); 
			htmlDoc.getElementsByTag("body").get(0).prependChild(drScriptRef);		
		}
				
		return htmlDoc.outerHtml();
	}

	private static byte[] zip(String inString, String fileName) throws IOException {     
       // logger.info("adaptedHtml: {}", inString);
        byte[] inBytes = inString.getBytes(StandardCharsets.UTF_8);
        ByteArrayInputStream bInStream = new ByteArrayInputStream(inBytes);
           
        ZipEntry zipEntry = new ZipEntry(fileName);     
              
        ByteArrayOutputStream bOutStream = new ByteArrayOutputStream();
        ZipOutputStream zOutStream = new ZipOutputStream(bOutStream, StandardCharsets.UTF_8);
        zOutStream.putNextEntry(zipEntry); 
        
        byte[] bytes = new byte[1024];
        int length;
        while((length = bInStream.read(bytes)) >= 0) {
        	zOutStream.write(bytes, 0, length);
        }
        zOutStream.closeEntry(); 
        zOutStream.close();
        bOutStream.close();
        bInStream.close();
       
        return bOutStream.toByteArray();
    }

	private static byte[] adaptZip(byte[] originalZip, int documentId) throws IOException {
        logger.info("########  adaptZip starting, documentId:{}, originalZip.length:{}", documentId, originalZip.length);
	
		if (originalZip.length > 5 * 1024 * 1024) {
			throw new IOException("zip files larger than 5MB are not accepted!");
		}

		InputStream inBytes = new ByteArrayInputStream(originalZip);
		ZipInputStream inZip = new ZipInputStream(inBytes);		

		//first iteration over the zip entries to find out the html file
		//and its upper Folders to remove from adapted zip structure.
		Path fakePath = Path.of("/");
		String htmlFileName = null;
		String upperFolders = null;
		for (ZipEntry inZipEntry = inZip.getNextEntry(); inZipEntry!=null; inZipEntry = inZip.getNextEntry()) {
			String entryName = inZipEntry.getName();
			Path entryPath = Path.of(entryName);
            zipSlipProtect(entryName, fakePath);
			if ( (entryName.endsWith(".htm") || entryName.endsWith(".html")) ) {
				htmlFileName = entryPath.getFileName().toString();
				upperFolders = entryName.substring(0, entryName.lastIndexOf(htmlFileName));
			} 
			inZip.closeEntry();
		}
		inZip.close();
		if (htmlFileName==null) {
			throw new IOException("zip file contains no html file!");
		}

		
		inBytes = new ByteArrayInputStream(originalZip);
		inZip = new ZipInputStream(inBytes);		
		ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
		ZipOutputStream outZip = new ZipOutputStream(outBytes);		
		
        byte[] entryBytes = null;
		ZipEntry outZipEntry = null;
		for (ZipEntry inZipEntry = inZip.getNextEntry(); inZipEntry!=null; inZipEntry = inZip.getNextEntry()) {
			String entryName = inZipEntry.getName();
            logger.info("########  inZipEntry.getName():{}",  inZipEntry.getName());
			if (entryName.endsWith(".html") || entryName.endsWith(".htm")) {
				entryName = documentId + ".html";
				entryBytes = inZip.readAllBytes();
				String originalHtml = new String(entryBytes, StandardCharsets.UTF_8);
				String originalCharSet = determineOriginalCharSet(originalHtml);
				originalHtml = new String(entryBytes, originalCharSet);
				String adaptedHtml = adaptHtml(originalHtml, originalCharSet);
				entryBytes = adaptedHtml.getBytes(StandardCharsets.UTF_8);
			} else {
				entryName = entryName.replaceFirst(upperFolders, "");
				entryBytes = inZip.readAllBytes();
			}
			if (entryName.trim() != "") { //to skip the root folder in the originalZip..
				outZipEntry = new ZipEntry(entryName);
				logger.info("######## outZipEntry.getName():{}", outZipEntry.getName());
				outZip.putNextEntry(outZipEntry);
				outZip.write(entryBytes);				
			}


			outZip.closeEntry();
			inZip.closeEntry();
		}
		outZip.close();
		outBytes.close();
		inZip.close();
		inBytes.close();
        logger.info("########  adaptZip ending, documentId:{}, adaptedZip.length:{}", documentId, outBytes.toByteArray().length);
		return outBytes.toByteArray();
	}	


	@Override
	public boolean isDocumentUnpacked(int documentId, String targetFolder) {
		return Files.exists(Path.of(targetFolder + "/" + documentId));
	}
	
	private static String determineOriginalCharSet(final String originalHtml) {
		
		org.jsoup.nodes.Document htmlDoc = Jsoup.parse(originalHtml);
		String charSetText = null;
		
		Elements charSetElements = htmlDoc.head().select("meta[charset]");
		if (charSetElements.size() == 1) {
			charSetText = charSetElements.get(0).attr("charset");			
		} else {
			charSetElements = htmlDoc.head().select("meta[http-equiv=Content-Type]");
			charSetText = charSetElements.get(0).attr("content");
			int beginIndex = charSetText.indexOf("charset");
			beginIndex = charSetText.indexOf('=', beginIndex) + 1;
			int endIndex = charSetText.indexOf(';', beginIndex);
			if (endIndex == -1) {
				charSetText = charSetText.substring(beginIndex).trim();
			} else {
				charSetText = charSetText.substring(beginIndex, endIndex).trim();
			}
		}
		logger.info("====================================originalCharSet: {}", charSetText);
		return charSetText;
	}
	
	

}
