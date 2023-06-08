package com.deepreadings.config;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

	
	@Bean
	public MessageSource messageSource() {
		ResourceBundleMessageSource resource = new ResourceBundleMessageSource();
		resource.setBasename("static/i18n/messages");
		resource.setDefaultEncoding("UTF-8");
		return resource;
	}	
	
	@Bean
	public LocaleResolver localeResolver() {
	    SessionLocaleResolver resolver = new SessionLocaleResolver();
	    resolver.setDefaultLocale(Locale.ENGLISH);
	    return resolver;
	}	
	
	@Bean
	public LocaleChangeInterceptor localeChangeInterceptor() {
	    LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
	    interceptor.setParamName("lang");
	    return interceptor;
	}
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
	    registry.addInterceptor(localeChangeInterceptor());
	}
	

}
