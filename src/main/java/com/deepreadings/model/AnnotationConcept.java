package com.deepreadings.model;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;


@Service
@Entity
@Table(name="ANNOTATION_CONCEPT")
public class AnnotationConcept implements Serializable {
	
	
	private static final long serialVersionUID = 20210820L;
	
	
	@Id
    @ManyToOne
    @JoinColumn(name = "ANNOTATION_ID",
            foreignKey = @ForeignKey(name = "FK_ANNOTATION_ID")
    )
	@JsonBackReference
	private Annotation annotation;
	
	
	@Id
    @ManyToOne
    @JoinColumn(name = "CONCEPT_ID",
            foreignKey = @ForeignKey(name = "FK_CONCEPT_ID")
    )
	@JsonManagedReference
	private Concept concept;	
	
	
	public AnnotationConcept () {}
	
	
	public AnnotationConcept (Annotation annotation, Concept concept) {
		this.annotation = annotation;
		this.concept = concept;
	}
	
	
	public Annotation getAnnotation() {
		return annotation;
	}


	public void setAnnotation(Annotation annotation) {
		this.annotation = annotation;
	}


	public Concept getConcept() {
		return concept;
	}

	
	public void setConcept(Concept concept) {
		this.concept = concept;
	}
	

	@Override
	public boolean equals(Object o) {
		if ( this == o ) {
			return true;
		}
		if ( o == null || getClass() != o.getClass() ) {
			return false;
		}
		AnnotationConcept that = (AnnotationConcept) o;
		return Objects.equals( annotation, that.getAnnotation() ) &&
				Objects.equals( concept, that.getConcept() );
	}


	@Override
	public int hashCode() {
		return Objects.hash(annotation, concept);
	}

}
