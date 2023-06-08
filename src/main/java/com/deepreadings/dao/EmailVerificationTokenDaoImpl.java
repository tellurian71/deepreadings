package com.deepreadings.dao;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Repository;

import com.deepreadings.model.EmailVerificationToken;

@Repository
@Transactional
public class EmailVerificationTokenDaoImpl extends JpaDao<EmailVerificationToken, Integer> implements EmailVerificationTokenDao {

	public EmailVerificationTokenDaoImpl() {
		this.setClazz(EmailVerificationToken.class);
	}

}
