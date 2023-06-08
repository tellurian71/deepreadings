package com.deepreadings.controller;

import java.security.Principal;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ResolvableType;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import com.deepreadings.dto.PasswordDto;
import com.deepreadings.dto.ReaderDto;
import com.deepreadings.model.ReaderDetails;
import com.deepreadings.model.Role;
import com.deepreadings.model.Reader;
import com.deepreadings.service.ReaderService;


@Controller
@RequestMapping("readers")
public class ReaderController {

	private static final Logger logger = LoggerFactory.getLogger(ReaderController.class);
 
	@Autowired
	ReaderService readerService;
	
//    @Autowired
//    private ClientRegistrationRepository clientRegistrationRepository;
 
//    @Autowired
//    private OAuth2AuthorizedClientService authorizedClientService;
    
    private static final String authorizationRequestBaseUri = "oauth2/authorize-client";
	Map<String, String> oauth2AuthenticationUrls = new HashMap<>();
	
	
	
	@RequestMapping(value="/login", method=RequestMethod.GET)
	public String getLoginPage(Locale locale, Model model) {
		return "login";
	}
	

//	@RequestMapping( value={"/oauth_login"}, method=RequestMethod.GET)
//	public String getOAuthLoginPage(Model model) {	
//        Iterable<ClientRegistration> clientRegistrations = null;
//        ResolvableType type = ResolvableType.forInstance(clientRegistrationRepository)
//            .as(Iterable.class);
//        if (type != ResolvableType.NONE && ClientRegistration.class.isAssignableFrom(type.resolveGenerics()[0])) {
//            clientRegistrations = (Iterable<ClientRegistration>) clientRegistrationRepository;
//        }
//
//        clientRegistrations.forEach(registration -> oauth2AuthenticationUrls.put(registration.getClientName(), authorizationRequestBaseUri + "/" + registration.getRegistrationId()));
//        model.addAttribute("urls", oauth2AuthenticationUrls);
//
//        return "oauth_login";
//	}	
	
//    @RequestMapping(value="/loginSuccess", method=RequestMethod.GET)
//    public String getLoginInfo(Model model, OAuth2AuthenticationToken authentication) {
//
//        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(authentication.getAuthorizedClientRegistrationId(), authentication.getName());
//
//        String userInfoEndpointUri = client.getClientRegistration()
//            .getProviderDetails()
//            .getUserInfoEndpoint()
//            .getUri();
//
//        if (!StringUtils.isEmpty(userInfoEndpointUri)) {
//            RestTemplate restTemplate = new RestTemplate();
//            HttpHeaders headers = new HttpHeaders();
//            headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + client.getAccessToken()
//                .getTokenValue());
//
//            HttpEntity<String> entity = new HttpEntity<String>("", headers);
//
//            ResponseEntity<Map> response = restTemplate.exchange(userInfoEndpointUri, HttpMethod.GET, entity, Map.class);
//            Map userAttributes = response.getBody();
//            model.addAttribute("name", userAttributes.get("name"));
//        }
//
//        return "loginSuccess";
//    }
	
	
	
	
	
	
	
	@RequestMapping( value={"/signUp"}, method=RequestMethod.GET)
	public String getSignupPage(Model model) {
		Reader newReader = new Reader();
		model.addAttribute("newReader", newReader);
		return "signUp";
	}	


	
	@RequestMapping( value={"/signUp"}, method=RequestMethod.POST)
	public String postSignUpPage(Model model, @ModelAttribute("newReader") final Reader returnedReader, final HttpServletRequest request) {	
        logger.info("Registering reader account with information: {}", returnedReader);
        if (readerService.readByName(returnedReader.getName()) != null) { //if reader already exists
        	return "signUp";
        }
        readerService.create(returnedReader);
        if (readerService.readByName(returnedReader.getName()) != null) { //if reader created successfully
        	readerService.verifyReader(returnedReader, getAppUrl(request), request.getLocale());
            model.addAttribute("signUpSuccess", true);
        	model.addAttribute("signUpEmail", returnedReader.getName());
            return "login";          
        }
        return "signUp";
    }

	
	
	
	@RequestMapping( value={"/emailVerification"}, method=RequestMethod.GET)
	public String confirmSignUp(final HttpServletRequest request, final Model model, @RequestParam("token") final String tokenString) {
		Locale locale = request.getLocale();
        model.addAttribute("lang", locale.getLanguage());
        Reader reader = readerService.verifyToken(tokenString);
        if (reader!=null) {
            authWithoutPassword(reader);
            model.addAttribute("messageKey", "message.accountVerified");
            return "redirect:/home";
        }

        model.addAttribute("messageKey", "auth.message." + reader);
        model.addAttribute("token", tokenString);
        return "redirect:/badUser";
    }
	
	
	
	
	
	@RequestMapping( value={"/profile"}, method=RequestMethod.GET)
	public String getProfile(Model model, Principal principal) {	

	
		logger.info("readerToEdit: {}", principal.getName());
 		Reader readerToEdit = readerService.readByName(principal.getName());
		model.addAttribute("readerToEdit", readerToEdit);
		logger.info("readerToEdit: {}", readerToEdit);

		List<ReaderDto> readerList = readerService.getReaderDtoList();
		readerList.remove(new ReaderDto(readerToEdit.getId(), readerToEdit.getName()));
		
		logger.info("THE SIZE OF READERLIST IS: {}", readerList.size());
		model.addAttribute("readerList", readerList);
		
		return "readerEditor";
	}
	
	
	
	@RequestMapping(value={"/profile"}, method=RequestMethod.POST)
	public String postProfile(@RequestParam Map<String, String> allParams, 
			Model model, 
			@AuthenticationPrincipal ReaderDetails loggedReader,
			@ModelAttribute("readerToEdit") Reader returnedReader, 
			BindingResult result) {	
		
		logger.info("allParams is: {}", allParams);
		logger.info("returnedReader: {}", returnedReader); 		

        if (result.hasErrors()) {
        	logger.info("error: {}", result);
        }		

		readerService.update(returnedReader);
		//loggedReader.setUsername(returnedReader.getName());
		return "redirect:/readers/profile";		
	}
	
	
	
	@RequestMapping(value={"/changePassword"}, method=RequestMethod.GET)
	public String getChangePassword(Model model, Principal principal) {	
		if (principal != null) {
			logger.info("principal.getName(): {}" , principal.getName());
			model.addAttribute("passwordDto", new PasswordDto());			
			return "changePassword";		
		} else {
			logger.info("principal is null." );
			return "redirect:/readers/login";
		}
	}
	
	
	
	@RequestMapping(value={"/changePassword"}, method=RequestMethod.POST)
	@PreAuthorize("authenticated()")
	public String postChangePassword(Model model, @ModelAttribute PasswordDto passwordDto, Principal principal, final Locale locale) {	
		Reader reader = readerService.readByName(principal.getName());
        if (!readerService.checkIfValidOldPassword(reader, passwordDto.getOldPassword())) {
            throw new RuntimeException("Old password invalid!");
        }
        readerService.updatePassword(reader, passwordDto.getNewPassword());
		model.addAttribute("changePasswordSuccess", true);
		logger.info("new password: {}", passwordDto.getNewPassword());
		return "login";
	}
	
	
	@RequestMapping(value={"/forgotPassword"}, method=RequestMethod.GET)
	public String getForgotPassword() {	
		return "forgotPassword";				
	}

	
	
	@RequestMapping(value={"/forgotPassword"}, method=RequestMethod.POST)
	public String postForgotPassword(
			Model model, 
			@RequestParam("name") String name) {
		Reader reader = readerService.readByName(name);
		if (reader==null) {
			model.addAttribute("result", "No reader found with the email you provided.");
		} else if (readerService.sendPasswordResetLink(name)) {
			model.addAttribute("result", "Password reset link has been sent to email address you just provided.");
		} else {
			model.addAttribute("result", "Email service failed. Please try again later.");
		}
		return "forgotPassword";				
	}
	

    private String getAppUrl(HttpServletRequest request) {
        return "https://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }

    
    public void authWithoutPassword(Reader reader) {

//        List<Privilege> privileges = reader.getRoles()
//                .stream()
//                .map(Role::getPrivileges)
//                .flatMap(Collection::stream)
//                .distinct()
//                .collect(Collectors.toList());
//
//        List<GrantedAuthority> authorities = privileges.stream()
//                .map(p -> new SimpleGrantedAuthority(p.getName()))
//                .collect(Collectors.toList());
   
    	logger.info("Reader: {}", reader);
    	logger.info("reader.getName(): {}", reader.getName());
        List<GrantedAuthority> authorities = reader.getRoles()
        		.stream()
        		.map(r->new SimpleGrantedAuthority(r.getName()))
        		.collect(Collectors.toList() );

        		
        Authentication authentication = new UsernamePasswordAuthenticationToken(reader, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
    
}

