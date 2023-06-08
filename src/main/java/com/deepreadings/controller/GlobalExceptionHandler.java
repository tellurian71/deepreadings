package com.deepreadings.controller;

import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice //spring already has a controller for errors.
public class GlobalExceptionHandler  {
	
	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	
	@ExceptionHandler(Throwable.class)
	public String handleException(
			HttpServletRequest request, 
			Exception ex,
			Model model ){
	
		logger.info("##############Exception Occured. URL="+request.getRequestURL());
		logger.info("##############Ex.toString: {} ", ex.toString());
		logger.info("##############Exc.getMessage: {} ", ex.getMessage());
		ex.printStackTrace();
		
//		StringWriter sw = new StringWriter();
//		PrintWriter pw = new PrintWriter(sw);
//		e.printStackTrace(pw);
//		logger.info("##############printStackTrace {} ", ex.getCause());

		String errorMessage = "";
        int errorCode = 0;
        
        if (request.getAttribute("javax.servlet.error.status_code") != null) {
        	errorCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        } 	
        
        switch (errorCode) {
        case 400: {
        	errorMessage = "Http Error Code: 400. Bad Request.";
            break;
        }
        case 401: {
        	errorMessage = "Http Error Code: 401. Unauthorized.";
            break;
        }
        case 403: {
        	errorMessage = "Http Error Code: 403. Access denied.";
            break;
        }
        case 404: {
        	errorMessage = "Http Error Code: 404. Resource not found.";
            break;
        }            
        case 405: {
        	errorMessage = "Http Error Code: 405. Method not allowed.";
            break;
        }
        case 500: {
        	errorMessage = "Http Error Code: 500. Internal Server Error.";
            break;
        }        
    }
    
    errorMessage += System.lineSeparator() + ex.getMessage() ;
    		
    		
    model.addAttribute("readerName", request.getUserPrincipal().getName() );
    model.addAttribute("errorCode", errorCode);
    model.addAttribute("errorMessage", errorMessage);

    return "error";
    }
    
}
