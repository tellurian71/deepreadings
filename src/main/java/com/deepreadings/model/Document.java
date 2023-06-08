 package com.deepreadings.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name="DOCUMENTS")

public class Document implements Serializable {
	
	@Column(name="ID")
	@Id
	@GeneratedValue
	int id;	
	
	@Column(name="TITLE")
	String title;
	
	@Column(name="AUTHOR")
	String author;
	
	@Column(name="LANGUAGE")
	String language;
	
	@Column(name="TRANSLATOR")
	String translator;
	
	@Column(name="MISC_INFO")
	String miscInfo;		
	

	@Lob 
	//@Basic(fetch=FetchType.LAZY)
	@Column(name="FILE_DATA", columnDefinition="LONGBLOB")
	@JsonIgnore 
	byte[] fileData;	
	
	@OneToMany(
		mappedBy = "document",
		fetch = FetchType.LAZY
	)
	@JsonBackReference
	private List<Annotation> annotations = new ArrayList<Annotation>();
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getAuthor() {
		return author;
	}
	
	public void setAuthor(String author) {
		this.author = author;
	}	
	
	public String getLanguage() {
		return language;
	}
	
	public void setLanguage(String language) {
		this.language = language;
	}
		
	public String getTranslator() {
		return translator;
	}
	
	public void setTranslator(String translator) {
		this.translator = translator;
	}
	
	public String getMiscInfo() {
		return miscInfo;
	}
	
	public void setMiscInfo(String miscInfo) {
		this.miscInfo = miscInfo;
	}	
	
	
	
	
	
	public byte[] getFileData() {
		return fileData;
	}
	
	public void setFileData(byte[] fileData) {
		this.fileData = fileData;
	}
	
	public List<Annotation> getAnnotations() {
		return annotations;
	}
	
	public void addAnnotation(Annotation a) {
		annotations.add(a);
		a.setDocument(this);
	}
	
	public void removeAnnotation(Annotation a) {
		annotations.remove(a);
		a.setDocument(null);
	}
	
	public String toString() {

		return "DOCUMENT-> Id: " + this.getId()
		+ ", Title: " + this.getTitle() 
		+ ", Author: " + this.getAuthor() 
		+ ", Lang: " + this.getLanguage() 
		+ ", fileData: " + ((fileData==null) ? 0: fileData.length) + " bytes.";
	}
	
}
