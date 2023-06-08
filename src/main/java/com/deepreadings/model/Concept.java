package com.deepreadings.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;


@Entity
@Table	(
	name = "CONCEPTS", 
	uniqueConstraints = @UniqueConstraint(
			name = "UK_CONCEPT_NAME_DESCRIPTION", 
			columnNames = {"NAME", "DESCRIPTION"}
	)
)
public class Concept {

	private static final Logger logger = LoggerFactory.getLogger(Concept.class);
	
	@Id
	@GeneratedValue	
	@Column(name="ID")
	private int id;	
	
	@Column(name="NAME", nullable=false)
	private String name;
	
	@JsonIgnore
	@Column(name="DESCRIPTION")
	private String description;	

	@Column(name="DATE_CREATED", nullable=false)
	private Instant dateCreated;		
	
	@JsonIgnore
	@ManyToOne
    @JoinColumn(name = "CREATED_BY", 
    		nullable=false,
            foreignKey = @ForeignKey(name = "FK_CONCEPT_CREATED_BY_READER")
    )	
	private Reader createdBy;
	
	@JsonIgnore
	@Column(name="DATE_UPDATED")
	private Instant dateUpdated;		
	
	@JsonIgnore
	@ManyToOne
    @JoinColumn(name = "UPDATED_BY", 
            foreignKey = @ForeignKey(name = "FK_CONCEPT_UPDATED_BY_READER")
    )	
	private Reader updatedBy;	
	
	@JsonIgnore
	@OneToMany(
		mappedBy = "lowerConcept",
		fetch = FetchType.LAZY,
		cascade = {CascadeType.PERSIST, CascadeType.MERGE},
		orphanRemoval = true  //if missing, hierarchy record is not deleted when concept1.removeUpperConcept(upper1) executed.
	)
	private List<ConceptHierarchy> upperConcepts = new ArrayList<ConceptHierarchy>();	

	@JsonIgnore
	@OneToMany(
		mappedBy = "upperConcept",
		fetch = FetchType.LAZY, 
		cascade = {CascadeType.PERSIST, CascadeType.MERGE},
		orphanRemoval = true  //if missing, hierarchy record is not deleted when concept1.removeUpperConcept(upper1) executed.
	)
	private List<ConceptHierarchy> lowerConcepts = new ArrayList<ConceptHierarchy>();
		
	
	@OneToMany(
			mappedBy = "concept", 
			fetch = FetchType.LAZY
			//cascade = CascadeType.ALL,
			//orphanRemoval = true 
	)
	@JsonBackReference	
	private List<AnnotationConcept> annotations = new ArrayList<AnnotationConcept>();
	

	public Concept() {
	}	
	
	public Concept(String name) {
		this.name = name;
	}
	
	
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public Instant getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Instant dateCreated) {
		this.dateCreated = dateCreated;
	}

	public Reader getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Reader createdBy) {
		this.createdBy = createdBy;
	}

	public Instant getDateUpdated() {
		return dateUpdated;
	}

	public void setDateUpdated(Instant dateUpdated) {
		this.dateUpdated = dateUpdated;
	}

	public Reader getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(Reader updatedBy) {
		this.updatedBy = updatedBy;
	}

	public List<ConceptHierarchy> getUpperConcepts() {
		return upperConcepts;
	}
	
	public List<ConceptHierarchy> getLowerConcepts() {
		return lowerConcepts;
	}
	
	public List<AnnotationConcept> getAnnotations() {
		return annotations;
	}

	
	public void addUpperConcept(Concept upperConcept) {
		ConceptHierarchy hierarchy = new ConceptHierarchy(upperConcept, this);
		upperConcept.getLowerConcepts().add(hierarchy);
		this.getUpperConcepts().add(hierarchy);
	}
	
	
	public void removeUpperConcept(Concept upperConcept) {
		ConceptHierarchy hierarchy = new ConceptHierarchy(upperConcept, this);
		upperConcept.getLowerConcepts().remove(hierarchy);
		this.getUpperConcepts().remove(hierarchy);
		hierarchy.setUpperConcept(null);
		hierarchy.setLowerConcept(null);
	}
	
	
	public void addLowerConcept(Concept lowerConcept) {
		ConceptHierarchy hierarchy = new ConceptHierarchy(this, lowerConcept);
		lowerConcept.getUpperConcepts().add(hierarchy);
		this.getLowerConcepts().add(hierarchy);
	}

	
	public void removeLowerConcept(Concept lowerConcept) {
		ConceptHierarchy hierarchy = new ConceptHierarchy(this, lowerConcept);
		lowerConcept.getUpperConcepts().remove(hierarchy);
		this.getLowerConcepts().remove(hierarchy);
		hierarchy.setUpperConcept(null);
		hierarchy.setLowerConcept(null);
	}	

	
	public boolean hasUpperConcept(int conceptId) {
		for (ConceptHierarchy ch: upperConcepts) {
			if (ch.getUpperConcept().getId() == conceptId) return true;
		}
		return false;
	}

	
	public boolean hasLowerConcept(int conceptId) {
		for (ConceptHierarchy ch: lowerConcepts) {
			if (ch.getLowerConcept().getId() == conceptId) return true;
		}
		return false;
	}
	
	
//	public void addAnnotation(Annotation annotation) {
//		AnnotationConcept annotationConcept = new AnnotationConcept(annotation, this);
//		this.getAnnotations().add(annotationConcept);
//		annotation.getConcepts().add(annotationConcept);
//	}
	

//	public void removeAnnotation(Annotation annotation) {
//		AnnotationConcept annotationConcept = new AnnotationConcept(annotation, this);
//		annotations.remove(annotationConcept);
//		annotation.getConcepts().remove(annotationConcept);
//		annotationConcept.setAnnotation(null);
//		annotationConcept.setConcept(null);
//	}
	
	@Override
	public String toString() {		
		String result =  "CONCEPT-> id: " + getId() + 
				", name: " + getName() + 
				", description: " + getDescription() +
				", dateCreated: " + getDateCreated() +
				", createdBy: " + getCreatedBy() +
				", dateUpdated: " + getDateUpdated() +
				", updatedBy: " + getUpdatedBy();
		
		for (ConceptHierarchy h: upperConcepts) {
			result += " UH:"+h.toString();
		}
		
		for (ConceptHierarchy h: lowerConcepts) {
			result += " LH:"+h.toString();
		}
		
		for (AnnotationConcept ac: annotations) {
			result += " AC:"+ ac.getAnnotation().getId() + "-" + ac.getConcept().getId();
		}
		
		result += System.lineSeparator();
		return result;
	}
	
	@Override
	public boolean equals(Object o) {
		if ( this == o ) {
			return true;
		}
		if ( o == null || getClass() != o.getClass() ) {
			return false;
		}
		Concept that = (Concept) o;
		return Objects.equals(id, that.getId()) && Objects.equals(name, that.getName());
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name);
	}

}
