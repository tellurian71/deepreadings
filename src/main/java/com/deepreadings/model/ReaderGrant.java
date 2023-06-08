package com.deepreadings.model;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
@Table(name="READER_GRANTS")
public class ReaderGrant implements Serializable {

	private static final Logger logger = LoggerFactory.getLogger(ReaderGrant.class);

	private static final long serialVersionUID = 20211211L;


	@Id
    @ManyToOne
    @JoinColumn(name = "GRANTOR_ID",
    		nullable = false,
            foreignKey = @ForeignKey(name = "FK_READER_GRANTS_GRANTOR_ID")
    )
	private Reader grantor;
	
	
	@Id
    @ManyToOne
    @JoinColumn(name = "GRANTEE_ID",
    		nullable = false,
            foreignKey = @ForeignKey(name = "FK_READER_GRANTS_GRANTEE_ID")
    )
	private Reader grantee;	



	@Column(name="TIMESTAMP", nullable=false)
	private Instant timestamp;	

	
	public ReaderGrant() {
		this.timestamp = Instant.now();
	}
	
	
	public ReaderGrant(Reader grantor, Reader grantee) {
		this.grantor = grantor;
		this.grantee = grantee;
		this.timestamp = Instant.now();
	}


	public Reader getGrantor() {
		return grantor;
	}


	public void setGrantor(Reader grantor) {
		this.grantor = grantor;
	}


	public Reader getGrantee() {
		return grantee;
	}


	public void setGrantee(Reader grantee) {
		this.grantee = grantee;
	}


	public Instant getTimestamp() {
		return timestamp;
	}


	public void setTimestamp(Instant timestamp) {
		this.timestamp = timestamp;
	}



	@Override
	public boolean equals(Object o) {
		if ( this == o ) {
			return true;
		}
		if ( o == null || getClass() != o.getClass() ) {
			return false;
		}
		ReaderGrant that = (ReaderGrant) o;
		
		int grantorId = 0;
		int granteeId = 0;
		if (grantor != null) {
			grantorId = grantor.getId();
		}
		if (grantee != null) {
			granteeId = grantee.getId();
		}
		
		int thatGrantorId = 0;
		int thatGranteeId = 0;
		if (that.getGrantor() != null) {
			thatGrantorId = that.getGrantor().getId();
		}
		if (that.getGrantee() != null) {
			thatGranteeId = that.getGrantee().getId();
		}		

		return 	Objects.equals( grantorId, thatGrantorId ) &&
				Objects.equals( granteeId, thatGranteeId )  ;
	}


	@Override
	public int hashCode() {
		return Objects.hash(grantor, grantee);
	}

	
	@Override
	public String toString() {		
		return "GRANTORREADER: " + getGrantor().getId() + ", GRANTEEREADER: "+ getGrantee().getId() + ", TIMESTAMP: " + getTimestamp();
	}
	
	
}

