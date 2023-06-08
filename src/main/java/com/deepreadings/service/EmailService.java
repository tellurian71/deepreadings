package com.deepreadings.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
public class EmailService {

    private static final String NOREPLY_ADDRESS = "noreply@deepreadings.com";
    private static final String SUPPORT_ADDRESS = "ahmet.dogan.ugurel@gmail.com";

    @Autowired
    private JavaMailSender mailSender;

    public void sendTextMail(final String replyTo, final String text) throws MailException {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setReplyTo((replyTo == "") ? NOREPLY_ADDRESS : replyTo);
		message.setTo(SUPPORT_ADDRESS);
		message.setSubject("Message from DeepReader");
		message.setText(text);
		mailSender.send(message);
    } 

    public void sendPasswordResetMail(final String readerEmail) throws MailException {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setReplyTo(NOREPLY_ADDRESS);
		message.setTo(readerEmail);
		message.setFrom("noreply@gmail.com");
		message.setSubject("DeepReader password reset link.");
		message.setText("text");
		mailSender.send(message);
    }
    
}





