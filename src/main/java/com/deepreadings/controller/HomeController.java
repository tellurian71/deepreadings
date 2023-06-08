package com.deepreadings.controller;

import java.io.IOException;
import java.security.Principal;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.deepreadings.service.EmailService;

@Controller("/")
public class HomeController {
	
    @Autowired
    private EmailService emailService; 
    
    
    @RequestMapping( value={"/", "/home"}, method=RequestMethod.GET)
	public String getHomePage(Locale locale, Model model) {		
		Date date = new Date();
		DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL, locale);
		String serverTime = formatter.format(date);
		model.addAttribute("serverTime", serverTime);		
		return "home";
	}

	
	@RequestMapping(value="/guidelines", method=RequestMethod.GET)
	public String getHelpPage(Locale locale, Model model) {		
		return "guidelines";
	}
	
	
	@RequestMapping(value="/contactUs", method=RequestMethod.GET)
	public String getContact(Locale locale, Model model) {
		return "contactUs";
	}


	@RequestMapping(value="/contactUs", method=RequestMethod.POST)
	public String postContact(
			HttpServletRequest request, 
			@RequestParam("email") final String email,
			@RequestParam("msgTxt") String msgTxt, 
			Principal principal)
	  throws MailException, IOException {

      String principalName = (principal==null)? "" : "FROM: " + principal.getName() + System.getProperty("line.separator");
        emailService.sendTextMail(email, principalName + msgTxt);
        return "redirect:contactUs?success=true";
	}	
	
	

}
