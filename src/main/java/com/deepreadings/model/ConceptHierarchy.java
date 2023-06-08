package com.deepreadings.model;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.ForeignKey;

@Entity
@Table(name="CONCEPT_HIERARCHY")
public class ConceptHierarchy implements Serializable {

	private static final Logger logger = LoggerFactory.getLogger(ConceptHierarchy.class);
	
	private static final long serialVersionUID = 1L;

	@JsonBackReference
	@Id
    @ManyToOne
    @JoinColumn(name = "UPPER_CONCEPT_ID",
            foreignKey = @ForeignKey(name = "FK_UPPER_CONCEPT_ID")
    )
	private Concept upperConcept;
	 
	@JsonBackReference
	@Id
    @ManyToOne
    @JoinColumn(name = "LOWER_CONCEPT_ID",
            foreignKey = @ForeignKey(name = "FK_LOWER_CONCEPT_ID")
    )
	private Concept lowerConcept;	
	
	@JsonIgnore
	@Transient
	private boolean markedForRemoval=false;

	public ConceptHierarchy() {		
	}
	
	public ConceptHierarchy(Concept upper, Concept lower) {
		this.upperConcept = upper;
		this.lowerConcept = lower;
	}


	public Concept getUpperConcept() {
		return upperConcept;
	}

	public void setUpperConcept(Concept upper) {
		this.upperConcept = upper;
	}
	
	public Concept getLowerConcept() {
		return lowerConcept;
	}

	public void setLowerConcept(Concept lower) {
		this.lowerConcept = lower;
	}
	
	public boolean isMarkedForRemoval() {
		return markedForRemoval;
	}

	public void setMarkedForRemoval(boolean markForRemoval) {
		this.markedForRemoval = markForRemoval;
	}
	
	@Override
	public String toString() {
		return  "  upper: " + ((upperConcept == null) ? "NULL" : upperConcept.getId() + "-" + upperConcept.getName()) + 
				"---lower: " + ((lowerConcept == null) ? "NULL" : lowerConcept.getId() + "-" + lowerConcept.getName()) +
				"---removalMark:" + isMarkedForRemoval(); 
	}

	@Override
	public boolean equals(Object o) {
		if ( this == o ) {
			return true;
		}
		if ( o == null || getClass() != o.getClass() ) {
			return false;
		}
		ConceptHierarchy that = (ConceptHierarchy) o;
		return Objects.equals( upperConcept, that.getUpperConcept() ) &&
				Objects.equals( lowerConcept, that.getLowerConcept() );
	}

	@Override
	public int hashCode() {
		return Objects.hash(upperConcept.getId(), lowerConcept.getId());
	}

}
