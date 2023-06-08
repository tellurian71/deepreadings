package com.deepreadings.config;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.*;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

//import com.deepreadings.service.ReaderDetailsService;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig
{	

	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.authorizeHttpRequests((request) -> request
				.requestMatchers("/", "/home", "/img/**").permitAll()
				.anyRequest().authenticated()
			)
			.formLogin((form) -> form
				.loginPage("/login").permitAll()
			)
//			.logout((logout) -> logout.permitAll())
			;

		return http.build();
	}	
	
	
	@Bean 
	public PasswordEncoder passwordEncoder() { 
	    return new BCryptPasswordEncoder(); 
	}	
	
	
//	@Autowired
//	DataSource dataSource;
//	
//    @Autowired
//    RestAuthenticationEntryPoint authenticationEntryPoint;
		
//	@Bean
//	public UserDetailsService userDetailsService() {
//		return new ReaderDetailsService();
//	}

	   
  
	   

//	@Autowired
//  protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
//		
//		auth.userDetailsService(userDetailsService());
//		
//      auth.jdbcAuthentication().dataSource(dataSource)
//      .usersByUsernameQuery("select name, password, enabled from READERS where name=?")
//      .authoritiesByUsernameQuery("select READERS.name, ROLES.name "
//      		+ "from READERS, READERS_ROLES, ROLES "
//      		+ "where READERS.id = READERS_ROLES.reader_id "
//      		+ "and READERS_ROLES.roles_id = ROLES.id "
//      		+ "and READERS.name=?");     	
//  }
	
	
	
	
	
    
//  @Override
//  public void configure(WebSecurity web) throws Exception {
//      web.ignoring().antMatchers("/resources/**"); 
//      //if resources are permitted by permitAll as below, the deepReader.js is causing MIME type error.
//  }
	
	
   
    
//  @Bean
//  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//	  http
//		//.csrf().disable()
//		.requiresChannel().anyRequest().requiresSecure() 
//	.and() 
//		.authorizeHttpRequests()
//		.requestMatchers("/resources/**").permitAll()
//		.requestMatchers("/home", "/guidelines", "/about", "/", "/readers/oauth_login", "/readers/login", "/readers/loginFailure", "/readers/signUp", "/readers/emailVerification", "/contactUs", "/readers/oauth2/authorize-client/google").permitAll()
//		.requestMatchers("/concepts").hasAuthority("ROLE_ADMIN_CONCEPT")
//		.requestMatchers("/documents/edit/**").hasAuthority("ROLE_ADMIN_DOCUMENT")
//		.requestMatchers("/documents/delete/**").hasAuthority("ROLE_ADMIN_DOCUMENT")
//		.requestMatchers("/documents").hasAuthority("ROLE_READER")
//		.requestMatchers("/rest/**").hasAuthority("ROLE_READER")
//		.anyRequest().authenticated()
//	.and()
//		.httpBasic()
//		.authenticationEntryPoint(authenticationEntryPoint)
//	.and()
//		.formLogin()
//		.loginPage("/readers/login")
//	.and()
//		.oauth2Login()
//		.loginPage("/readers/oauth_login")
//		.authorizationEndpoint()
//		.baseUri("/oauth2/authorize-client")
//		.authorizationRequestRepository(authorizationRequestRepository())
//   .and()
//      .tokenEndpoint()
//      .accessTokenResponseClient(accessTokenResponseClient())
//   .and()
//		.defaultSuccessUrl("/home", false)
//		.failureUrl("/readers/login?error=true")
//	.and()	
//		.exceptionHandling()
//		.accessDeniedPage("/error?webSecErrorCode=403")
//	.and()
//  		.logout()
//  		.deleteCookies("JSESSIONID");      
//      
//  	return http.build();
//  }    
    

	
	
//    @Bean
//    public AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository() {
//        return new HttpSessionOAuth2AuthorizationRequestRepository();
//    }
//
//    @Bean
//    public OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient() {
//        DefaultAuthorizationCodeTokenResponseClient accessTokenResponseClient = new DefaultAuthorizationCodeTokenResponseClient();
//        return accessTokenResponseClient;
//    }  
//
//	
//
//	// additional configuration for non-Spring Boot projects
//    private static List<String> clients = Arrays.asList("google", "facebook");
//    
//    @Bean
//	public ClientRegistrationRepository clientRegistrationRepository() {
//      List<ClientRegistration> registrations = clients.stream()
//          .map(c -> getRegistration(c))
//          .filter(registration -> registration != null)
//          .collect(Collectors.toList());
//
//      return new InMemoryClientRegistrationRepository(registrations);
//	}	
//	
//	@Bean
//	public OAuth2AuthorizedClientService authorizedClientService() {
//		return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository());
//	}
//  
//    private static String CLIENT_PROPERTY_KEY = "spring.security.oauth2.client.registration.";
//
//    private ClientRegistration getRegistration(String client) {
//        String clientId = env.getProperty(CLIENT_PROPERTY_KEY + client + ".client-id");
//
//        if (clientId == null) {
//            return null;
//        }
//
//        String clientSecret = env.getProperty(CLIENT_PROPERTY_KEY + client + ".client-secret");
//        if (client.equals("google")) {
//            return CommonOAuth2Provider.GOOGLE.getBuilder(client)
//                .clientId(clientId)
//                .clientSecret(clientSecret)
//                .build();
//        }
//        if (client.equals("facebook")) {
//            return CommonOAuth2Provider.FACEBOOK.getBuilder(client)
//                .clientId(clientId)
//                .clientSecret(clientSecret)
//                .build();
//        }
//        return null;
//    }	
//	
//	
}
