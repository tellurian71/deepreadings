package com.deepreadings.service;

import java.util.List;
import java.util.Locale;

import com.deepreadings.dto.ReaderDto;
import com.deepreadings.model.Reader;

public interface ReaderService {
    
    public static final String TOKEN_INVALID = "invalidToken";
    public static final String TOKEN_EXPIRED = "expired";
    public static final String TOKEN_VALID = "valid";
    
    public void create(Reader newReader);
	public Reader read(int id);
	public Reader readByName(String name);
	public Reader readByToken(String token);
	public List<ReaderDto> getReaderDtoList();
	public Reader update(Reader reader);
	public boolean checkIfValidOldPassword(Reader reader, String oldPassword);
	public void updatePassword(Reader reader, String newPassword);
	public boolean sendPasswordResetLink(String readerEmail);
	public Reader verifyToken(String token);
	public String createVerificationTokenForReader(final Reader reader);
    public void verifyReader(final Reader reader, final String appUrl, final Locale locale);
}