package com.deepreadings.model;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Calendar;
import java.util.Date;

import jakarta.persistence.*;

@Entity
@Table	(name="EMAILVERIFICATIONTOKEN")
public class EmailVerificationToken {

    private static final int EXPIRATION = 60 * 24;

	@Column(name="ID")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

	@Column(name="TOKEN")
    private String token;

    @OneToOne(targetEntity = Reader.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "READER_ID", foreignKey = @ForeignKey(name = "FK_TOKEN_READER"))
    private Reader reader;

	@Column(name="EXPIRY_DATE")
    private Instant expiryDate;

    public EmailVerificationToken() {
        super();
    }

    public EmailVerificationToken(final String token, final Reader reader) {
        super();

        this.token = token;
        this.reader = reader;
        this.expiryDate = calculateExpiryDate(EXPIRATION);
    }

    public int getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(final String token) {
        this.token = token;
    }

    public Reader getReader() {
        return reader;
    }

    public void setReader(final Reader reader) {
        this.reader = reader;
    }

    public Instant getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(final Instant expiryDate) {
        this.expiryDate = expiryDate;
    }

    private Date calculateExpiryDate2(final int expiryTimeInMinutes) {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(new Date().getTime());
        cal.add(Calendar.MINUTE, expiryTimeInMinutes);
        return new Date(cal.getTime().getTime());
    }
    private Instant calculateExpiryDate(final int expiryTimeInMinutes) {
        final Instant now = Instant.now();
        Instant expiry = now.plus(expiryTimeInMinutes, ChronoUnit.MINUTES);
        return expiry;
    }

    public void updateToken(final String token) {
        this.token = token;
        this.expiryDate = calculateExpiryDate(EXPIRATION);
    }

    //

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getExpiryDate() == null) ? 0 : getExpiryDate().hashCode());
        result = prime * result + ((getToken() == null) ? 0 : getToken().hashCode());
        result = prime * result + ((getReader() == null) ? 0 : getReader().hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final EmailVerificationToken other = (EmailVerificationToken) obj;
        if (getExpiryDate() == null) {
            if (other.getExpiryDate() != null) {
                return false;
            }
        } else if (!getExpiryDate().equals(other.getExpiryDate())) {
            return false;
        }
        if (getToken() == null) {
            if (other.getToken() != null) {
                return false;
            }
        } else if (!getToken().equals(other.getToken())) {
            return false;
        }
        if (getReader() == null) {
            if (other.getReader() != null) {
                return false;
            }
        } else if (!getReader().equals(other.getReader())) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Token: ").append(token).append("-")
        .append("reader: ").append(reader.getName()).append("-")
        .append("expiry: ").append(expiryDate);
        return builder.toString();
    }

}