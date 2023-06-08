package com.deepreadings.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.deepreadings.model.ReaderDetails;
import com.deepreadings.dao.ReaderDao;
import com.deepreadings.model.Reader;


public class ReaderDetailsService implements UserDetailsService {

	@Autowired
	ReaderDao readerDao;
	
	private static final Logger logger = LoggerFactory.getLogger(ReaderDetailsService.class);
	
	@Override
	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
		logger.info("readerName: {}", userName);
		Reader reader = readerDao.readByName(userName);
		if (reader != null) {
			return new ReaderDetails(reader);
		}
		throw new UsernameNotFoundException("Could not find reader: " + userName);
	}
	
}

