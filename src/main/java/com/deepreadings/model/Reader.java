package com.deepreadings.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.annotation.JsonIgnore;



@Entity
@Table	(name="READERS")
public class Reader {

	private static final Logger logger = LoggerFactory.getLogger(Reader.class);	
	
	@Id
	@Column(name="ID")
	@GeneratedValue(
			strategy = GenerationType.SEQUENCE,
			generator = "readerSequenceGenerator"
	)
	@SequenceGenerator(
		name = "readerSequenceGenerator",
		sequenceName = "READER_SEQ", 
		allocationSize = 1
	)	
	private int id;	

	@Column(name="NAME", unique = true)
	private String name;
	
	@Column(name="ALIAS")
	private String alias;
	
	@JsonIgnore
	@Column(name="PASSWORD")
	private String password;
	
	@JsonIgnore
	@Column(name="ENABLED")
	private boolean enabled;
	
	@Column(name="GRANTPOLICY")
	//@Pattern(regexp="\\b\\d{10}\\b") jakarta.validation package is requied i think. fix pom setup first..
	private String grantPolicy = "SELECTION";
	
	@Column(name="DATE_CREATED", nullable=false)
	private Instant dateCreated;

	@Column(name="DATE_VERIFIED")
	private Instant dateVerified;
	
	@JsonIgnore
	@ManyToMany(
			cascade = {CascadeType.PERSIST, CascadeType.MERGE}, 
			fetch=FetchType.EAGER
	)
	private Set<Role> roles = new HashSet<Role>();
	
	@JsonIgnore
	@OneToMany(
			mappedBy = "grantor", 
			cascade = CascadeType.ALL, 
			//fetch = FetchType.EAGER,
			orphanRemoval=true)
	private List<ReaderGrant> grantees = new ArrayList<ReaderGrant>();	

	@JsonIgnore
	@OneToMany(
			mappedBy = "grantee", 
			cascade = CascadeType.ALL,
			//fetch = FetchType.EAGER,
			orphanRemoval=true)
	private List<ReaderGrant> grantors = new ArrayList<ReaderGrant>();
	
	@JsonIgnore
	@OneToMany(
			mappedBy = "owner", 
			cascade = CascadeType.ALL, 
			//fetch = FetchType.EAGER,
			orphanRemoval=true)
	private List<Annotation> annotations = new ArrayList<Annotation>();	


	public Reader() {
		super();
		this.enabled = false; //reader disabled until email verification.
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
	
	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	
	public Instant getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Instant dateCreated) {
		this.dateCreated = dateCreated;
	}

	public Instant getDateVerified() {
		return dateVerified;
	}

	public void setDateVerified(Instant dateVerified) {
		this.dateVerified = dateVerified;
	}
	
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getGrantPolicy() {
		return grantPolicy;
	}

	public void setGrantPolicy(String grantPolicy) {
		this.grantPolicy = grantPolicy;
	}

	public Set<Role> getRoles() {
		return roles;
	}
		
	public void addRole(Role role) {
		this.getRoles().add(role);
	}
	
	public List<ReaderGrant> getGrantees() {
		return grantees;
	}	
	
	public void setGrantees(List<ReaderGrant> grantees) {
		this.grantees = grantees;
	}	
	
	public List<ReaderGrant> getGrantors() {
		return grantors;
	}

	public void setGrantors(List<ReaderGrant> grantors) {
		this.grantors = grantors;
	}
	
	public List<Annotation> getAnnotations() {
		return annotations;
	}
	
	public void addGrantee(Reader grantee) {
		ReaderGrant readerGrant = new ReaderGrant(this, grantee);
		this.getGrantees().add(readerGrant);
		grantee.getGrantors().add(readerGrant);
	}
	

	
	public void removeGrantee(Reader grantee) {
		ReaderGrant readerGrant = new ReaderGrant(this, grantee);
		grantee.getGrantors().remove(readerGrant);
		this.getGrantees().remove(readerGrant);
		readerGrant.setGrantor(null);
		readerGrant.setGrantee(null);
		readerGrant.setTimestamp(null);
	}
	
	
	public boolean isReadAccessGrantedTo(Reader requestor) {
		//if (this.equals(requestor)) {
		if (Objects.equals(this, requestor)) {
			return true;
		} else if (this.grantPolicy=="ALL") {
			return true;
		} else if (this.grantPolicy=="NONE") {
			return false;
		} else { // grantPolicy=="SELECTION"
//			this.getGrantees().forEach(ug -> {logger.info("USERGRANT: {}", ug);});
//			ReaderGrant newUserGrant=new ReaderGrant(this,requestor);
//			boolean result = this.getGrantees().contains(newUserGrant);
//			logger.info("NEW USERGRANT: {}", newUserGrant );
//			logger.info("RESULT: {}", result );
			return this.getGrantees().contains(new ReaderGrant(this, requestor));
		}
	}
	
	
	@Override
	public boolean equals(Object o) {
		if ( this == o ) {
			return true;
		}
		if ( o == null || getClass() != o.getClass() ) {
			return false;
		}

		Reader that = (Reader) o;
		return Objects.equals( this.getId(), that.getId() ) &&
				Objects.equals( this.getName(), that.getName());
	}

	
	@Override
	public int hashCode() {
		return Objects.hash(id, name);
	}	
	

	public String toString() {
		StringBuilder rolesString = new StringBuilder();
		StringBuilder granteesString = new StringBuilder();
		StringBuilder grantorsString = new StringBuilder();
		
		roles.forEach(r->rolesString.append(r.getName()).append('-'));
		grantees.forEach( g-> {
			granteesString.append("  ");
			if (g.getGrantor() == null) {
				granteesString.append("nullGrantorReader-");	
			} else {
				granteesString.append(g.getGrantor().getId()).append("-");
			}
			if (g.getGrantee() == null) {
				granteesString.append("nullGranteeReader");	
			} else {
				granteesString.append(g.getGrantee().getId());
			}			
		});
		grantors.forEach( g-> {
			grantorsString.append("  ");
			if (g.getGrantor() == null) {
				grantorsString.append("nullGrantorReader-");	
			} else {
				grantorsString.append(g.getGrantor().getId()).append("-");
			}
			if (g.getGrantee() == null) {
				grantorsString.append("nullGranteeReader");	
			} else {
				grantorsString.append(g.getGrantee().getId());
			}			
		});	
		
		return 	"ID:" + getId() + 
				" NAME:" + getName() + 
				//" PASSWORD:" + password + 
				//" ENABLED:" + enabled + 
				" Policy:" + getGrantPolicy() +
				" Roles:" + rolesString + 
				" Grantees:" + granteesString + 
				" Grantors:" + grantorsString + 
				System.lineSeparator(); 
	}
	
}

