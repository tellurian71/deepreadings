package com.deepreadings.dao;

import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import com.deepreadings.model.EmailVerificationToken;

//public interface EmailVerificationTokenDao extends JpaRepository<EmailVerificationToken, String> {
//
//	EmailVerificationToken findByToken(String token);
//
//	EmailVerificationToken findByReader(Reader reader);
//
//	Stream<EmailVerificationToken> findAllByExpiryDateLessThan(Date now);
//
//	void deleteByExpiryDateLessThan(Date now);
//
//	@Modifying
//	@Query("delete from EmailVerificationToken t where t.expiryDate <= ?1")
//	void deleteAllExpiredSince(Date now);
//}


public interface EmailVerificationTokenDao {

	List<EmailVerificationToken> readAll();
	EmailVerificationToken read(Integer tokenId);
	void create(EmailVerificationToken token);
	void update(EmailVerificationToken token);
	void delete(EmailVerificationToken token);
	
	<P> EmailVerificationToken readByProperty(String propertyName, P property);

}
