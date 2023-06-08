package com.deepreadings.model;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name="ANNOTATIONS")
public class Annotation implements Serializable {
	
	private static final Logger logger = LoggerFactory.getLogger(Annotation.class);
	
	private static final long serialVersionUID = 20220315L;
		
	@Id
	@Column(name="ID")
	@GeneratedValue(
			strategy = GenerationType.SEQUENCE,
			generator = "annotationSequenceGenerator"
	)
	@SequenceGenerator(
		name = "annotationSequenceGenerator",
		sequenceName = "ANNOTATION_SEQ"
	)	
	private long id;
	
	@Column(name="TYPE", nullable=false)
	private String type;
	
	@Column(name="START_NODE_ID", nullable=false)
	private String startNodeId;
	
	@Column(name="START_OFFSET", nullable=false)
	private String startOffset;
	
	@Column(name="END_NODE_ID", nullable=false)
	private String endNodeId;
	
	@Column(name="END_OFFSET", nullable=false)
	private String endOffset;
		
	@Column(name="COMMENT")
	private String comment;

	@Column(name="TIMESTAMP", nullable=false)
	private Instant timestamp;	


	@ManyToOne (fetch = FetchType.EAGER)
    @JoinColumn(name = "DOCUMENT_ID", 
    		nullable=false,
            foreignKey = @ForeignKey(name = "FK_ANNOTATION_DOCUMENT_ID")
    )
    @JsonIgnore
    private Document document;	
    
    
	@OneToMany(
		mappedBy = "annotation",
		fetch = FetchType.LAZY,		
		cascade = CascadeType.ALL,
		orphanRemoval = true
	)
	@JsonManagedReference
	private List<AnnotationConcept> concepts = new ArrayList<AnnotationConcept>();   
	
	@ManyToOne
    @JoinColumn(name = "READER_ID", 
    		nullable=false,
            foreignKey = @ForeignKey(name = "FK_ANNOTATION_READER_ID")
    )	
	private Reader owner;
  
	
	public Annotation() {};
	
    @JsonCreator
    public Annotation(
    		@JsonProperty("documentId") 	int documentId,
    		@JsonProperty("type")			String type,
    		@JsonProperty("startNodeId") 	String startNodeId,
    		@JsonProperty("startOffset") 	String startOffset,
    		@JsonProperty("endNodeId") 		String endNodeId,
    		@JsonProperty("endOffset") 		String endOffset,
    		@JsonProperty("comment")		String comment, 
			@JsonProperty("concepts")  		ArrayList<String> conceptNames) 
    {
    	this.document = new Document();
    	this.document.setId(documentId);
    	
    	this.type = type;
    	this.startNodeId = startNodeId;
    	this.startOffset = startOffset;
    	this.endNodeId = endNodeId;
    	this.endOffset = endOffset;
    	this.comment = comment;
    	
    	if (conceptNames != null) {
    		conceptNames.forEach(cName -> {
    			this.addConcept(new Concept(cName));
    		});
    	}
    }
    
	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getStartNodeId() {
		return startNodeId;
	}

	public void setStartNodeId(String startNodeId) {
		this.startNodeId = startNodeId;
	}

	public String getStartOffset() {
		return startOffset;
	}

	public void setStartOffset(String startOffset) {
		this.startOffset = startOffset;
	}

	public String getEndNodeId() {
		return endNodeId;
	}

	public void setEndNodeId(String endNodeId) {
		this.endNodeId = endNodeId;
	}

	public String getEndOffset() {
		return endOffset;
	}

	public void setEndOffset(String endOffsetPath) {
		this.endOffset = endOffsetPath;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Reader getOwner() {
		return owner;
	}

	public void setOwner(Reader owner) {
		this.owner = owner;
	}
	
	public List<AnnotationConcept> getConcepts() {
		return concepts;
	}
	
	@JsonIgnore
	public List<String> getConceptNames() {
		List<String> conceptNames = new ArrayList<String>();
		concepts.forEach(c -> { 
			conceptNames.add(c.getConcept().getName());
		});
		return conceptNames;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public Instant getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Instant timestamp) {
		this.timestamp = timestamp;
	}
	
	public void addConcept(Concept concept) {
		AnnotationConcept annotationConcept = new AnnotationConcept(this, concept);
		this.getConcepts().add(annotationConcept);
		concept.getAnnotations().add(annotationConcept);
	}

	public void removeConcept(Concept concept) {
		AnnotationConcept annotationConcept = new AnnotationConcept(this, concept);
		concept.getAnnotations().remove(annotationConcept);
		concepts.remove(annotationConcept);
		annotationConcept.setAnnotation(null);
		annotationConcept.setConcept(null);
	}
	
	public String toString() {
		Document document = this.getDocument();
//		StringBuffer conceptsStr = new StringBuffer();
//		this.getConcepts().forEach(ac -> {
//			conceptsStr.append(ac.getConcept().toString()).append('-');
//		});
		String conceptsStr = "";
		for (AnnotationConcept ac: this.getConcepts()) {
			if (ac==null) {
				conceptsStr += "ac is null!" ;
			} else if (ac.getConcept()==null){
				conceptsStr += "ac.getConcept() returned null!" ;
			} else {
				conceptsStr += ac.getConcept().toString() + "-" ;
			}
		}
		
		return "ANNOTATION-> DocId-Id:" 
				+ ((document==null) ? 0 : this.getDocument().getId()) + "-" 
				+ this.getId() + ", Type: " + this.getType() 
				+ ", Start: " + this.getStartNodeId()+ "-" + this.getStartOffset() 
				+ ", End: " + this.getEndNodeId() + "-" + this.getEndOffset() 
				+ ", Reader: " + (this.getOwner()==null ? null : this.getOwner().getName())
				+ ", Comment: " + this.getComment()
				+ ", ConceptCount: " + this.getConcepts().size()
				+ ", Concepts: " + conceptsStr
				+ ", Timestamp: " + this.getTimestamp();
	}

}
