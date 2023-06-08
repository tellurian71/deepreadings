package com.deepreadings.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import jakarta.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.deepreadings.dao.EmailVerificationTokenDao;
import com.deepreadings.dao.ReaderDao;
import com.deepreadings.dto.ReaderDto;
import com.deepreadings.model.Role;
import com.deepreadings.model.EmailVerificationToken;
import com.deepreadings.model.Reader;
import com.deepreadings.model.ReaderGrant;

@Transactional
@Service
public class ReaderServiceImpl implements ReaderService {
	
	private static final Logger logger = LoggerFactory.getLogger(ReaderServiceImpl.class);

	@Autowired
	private ReaderDao readerDao;	
	
	@Autowired
	private RoleService roleService;
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailVerificationTokenDao tokenDao;
    
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private MessageSource messages;

    @Autowired
    private Environment env;  

    
    @Override
	public void create(Reader newReader) {
		String encodedPassword = passwordEncoder.encode(newReader.getPassword());
		newReader.setPassword(encodedPassword);
		newReader.setDateCreated(Instant.now());
		Role readerRole = roleService.readByName("ROLE_READER");
		newReader.addRole(readerRole);
		readerDao.create(newReader);
	};


	@Override
	public Reader read(int id) {
		Reader managedReader = readerDao.read(id);
		
		//trigger the lazy fetching of child associations
		managedReader.getRoles().size(); //not lazy
		managedReader.getGrantors().size();
		managedReader.getGrantees().size();
		logger.info("managedReader: {}", managedReader);
		return managedReader;		
	}
	


	@Override
	public Reader readByName(String name) {
		Reader managedReader = readerDao.readByName(name);
		if (managedReader != null) {
			//trigger the lazy fetching of child associations
			managedReader.getRoles().size(); //not lazy
			managedReader.getGrantors().size();
			managedReader.getGrantees().size();
		}
		logger.info("managedReader: {}", managedReader);
		return managedReader;
	}
	
    @Override
    public Reader readByToken(final String tokenString) {
        final EmailVerificationToken token = tokenDao.readByProperty("token", tokenString);
		logger.info("#####token read by property: {}", token);
		if (token != null) {
    	   Reader managedReader = token.getReader();
   			if (managedReader != null) {
   				//trigger the lazy fetching of child associations
   				managedReader.getRoles().size(); //not lazy
   				managedReader.getGrantors().size();
   				managedReader.getGrantees().size();
   			}  	   
            return managedReader;
        }
        return null;
    }	

	@Override
	public List<ReaderDto> getReaderDtoList() {
		List<ReaderDto> allReadersDto = new ArrayList<ReaderDto>();
		
		List<Reader> allReaders = readerDao.readAll();
		allReaders.forEach(reader -> {
			ReaderDto readerDto = new ReaderDto(reader.getId(), reader.getName());
			allReadersDto.add(readerDto);
		});
		return allReadersDto;
	};	
	
	@Override
	public Reader update(Reader detachedUser) {		
	
		Reader managedUser = this.read(detachedUser.getId());
		managedUser.setName(detachedUser.getName());
		managedUser.setGrantPolicy(detachedUser.getGrantPolicy());
		logger.warn("detachedUser: {}", detachedUser);
		logger.warn("managedUser: {}", managedUser);
		
		ReaderGrant grant = null;
		if (detachedUser.getGrantPolicy().equals("NONE")) {			
			//clear granteesList
			for(int i = managedUser.getGrantees().size()-1; i>=0; i--) {
				grant = managedUser.getGrantees().get(i);
				managedUser.removeGrantee(grant.getGrantee());				
			}
			logger.warn("detachedUser: {}", detachedUser);
			logger.warn("managedUser: {}", managedUser);
		} else if (detachedUser.getGrantPolicy().equals("SELECTION"))	{
			
			//first get rid of the null grantees(rows deleted from the form still occupy slots in the array)
			for(int i = detachedUser.getGrantees().size()-1; i>=0; i--) {
				if (detachedUser.getGrantees().get(i).getGrantee() == null) {
					detachedUser.getGrantees().remove(i);
				}
			}							
			logger.warn("detachedUser: {}", detachedUser);
			logger.warn("managedUser: {}", managedUser);
			
			//grantees not in the detachedUser must be removed from managedUser
			for(int i = managedUser.getGrantees().size()-1; i>=0; i--) {
				grant = managedUser.getGrantees().get(i);
				if (!detachedUser.getGrantees().contains(grant)) {
					logger.info("grant to be removed: {}", grant);
					managedUser.removeGrantee(grant.getGrantee());				
				}
			}
			logger.warn("detachedUser: {}", detachedUser);
			logger.warn("managedUser: {}", managedUser);
			
			//grantees of detachedUser which are not in managedUser must be added.
			for(int i = detachedUser.getGrantees().size()-1; i>=0; i--) {
				grant = detachedUser.getGrantees().get(i);
				if (!managedUser.getGrantees().contains(grant)) {
					managedUser.addGrantee(grant.getGrantee());
				}
			}		
			logger.warn("detachedUser: {}", detachedUser);
			logger.warn("managedUser: {}", managedUser);

		}
		return managedUser;
	}


	@Override
	public boolean checkIfValidOldPassword(Reader reader, String oldPassword) {
        return passwordEncoder.matches(oldPassword, reader.getPassword());
	}
	

	@Override
	public void updatePassword(Reader managedReader, String newPassword) {
		managedReader.setPassword(passwordEncoder.encode(newPassword));
	}

	
	@Override
	public boolean sendPasswordResetLink(String email) {
		try {
			emailService.sendPasswordResetMail(email);
			return true;
		} catch (Exception e) {
			logger.error("sendPasswordResetLink failed ({})", email);
		}
		return false;
	}



    @Override
    public String createVerificationTokenForReader(final Reader reader) {
        final String token = UUID.randomUUID().toString();
        final EmailVerificationToken emailVerificationToken = new EmailVerificationToken(token, reader);
        tokenDao.create(emailVerificationToken);
        return token;
    }
    
    
    @Override
    public Reader verifyToken(String tokenString) {
        final EmailVerificationToken emailVerificationToken = tokenDao.readByProperty("token", tokenString);
        if (emailVerificationToken == null) {
            return null;
        }

        Reader reader = emailVerificationToken.getReader();
        reader = read(reader.getId()); // second read is done to fetch lazy associations.
        final Instant now = Instant.now();
        if (emailVerificationToken.getExpiryDate().isBefore(now)) {
            //tokenDao.delete(emailVerificationToken);
            return null;
        }

        reader.setEnabled(true);
        reader.setDateVerified(now);
        readerDao.update(reader);
        return reader;
    }
    

    @Override
    public void verifyReader(final Reader reader, final String appUrl, final Locale locale) {
        final String token = this.createVerificationTokenForReader(reader);
        final SimpleMailMessage email = constructEmailMessage(appUrl, locale, reader, token);
        mailSender.send(email);
    }   
    


    private SimpleMailMessage constructEmailMessage(final String appUrl, final Locale locale, final Reader reader, final String token) {
        final String to = reader.getName();
        final String subject = "Deepreader Signup Confirmation";
        final String confirmationUrl = appUrl + "/readers/emailVerification?token=" + token;
        final String message = messages.getMessage("signup.succLink.message", null, "You signed up successfully. To confirm your signup email, please click on the below link.", locale);
        final SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(to);
        email.setSubject(subject);
        email.setText(message + System.lineSeparator() + confirmationUrl);
        email.setFrom(env.getProperty("support.email"));
        return email;
    } 	
	
	
}

