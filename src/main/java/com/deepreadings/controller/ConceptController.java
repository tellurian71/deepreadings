package com.deepreadings.controller;

import java.security.Principal;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.deepreadings.model.Concept;
import com.deepreadings.service.ConceptService;


@Controller
@RequestMapping("concepts")
public class ConceptController {
	
	private static final Logger logger = LoggerFactory.getLogger(ConceptController.class);

	@Autowired
	ConceptService conceptService;	
	
	@RequestMapping(value="", method=RequestMethod.GET)
	public String getConcepts(Locale locale, Model model) {
		List<Concept> concepts = conceptService.readAll();
		model.addAttribute("concepts", concepts);		
		return "concepts";
	}	
	
	
	@RequestMapping(value="/edit/{conceptId}", method=RequestMethod.GET)
	public String conceptEditGet(Model model, Locale locale, @PathVariable(value="conceptId") int conceptId) {
		
		Concept conceptToEdit;
		if (conceptId==0) { 
			conceptToEdit = new Concept(); 
		} else {
			conceptToEdit = conceptService.read(conceptId);
		}

		logger.info("conceptToEdit:{}", conceptToEdit);
		model.addAttribute("conceptToEdit", conceptToEdit);
		model.addAttribute("useCount", conceptToEdit.getAnnotations().size() );
		
		//complete set of concepts added to the model so as to manage the hierarchies in the conceptEditor.
		List<Concept> allConcepts = conceptService.readAll();
		model.addAttribute("allConcepts", allConcepts);
		
		logger.info("/concepts/edit-GET.  The client locale: {}. model:{}", locale, model);
		return "conceptEditor";
	}


	@RequestMapping(value="/edit/{conceptId}", method=RequestMethod.POST)
	public String conceptEditPost(@RequestParam Map<String, String> allParams, 
			@ModelAttribute("conceptToEdit") Concept returnedConcept, 
			Principal principal) {
		
		logger.info("The allParams is: {}", allParams);
		logger.info("The returnedConcept is: {}", returnedConcept);
		logger.info("The principal.getName() is: {}", principal.getName());
		
		//if this is a new concept to be created:
		if (returnedConcept.getId()==0) {
			conceptService.create(returnedConcept, principal);
			logger.info("new concept: {}", returnedConcept);
		} else {
			conceptService.update(returnedConcept, principal);
		}
		return "redirect:/concepts";	
	}
	
	
	
	@RequestMapping(value="/delete/{conceptId}", method=RequestMethod.POST)
	public String deleteConcept(@PathVariable(value = "conceptId") int conceptId) {
				
		logger.info("/concepts/delete-POST. conceptID:{}", conceptId);

		Concept conceptToDelete = conceptService.read(conceptId);
		conceptService.delete(conceptToDelete);
		
		return "redirect:/concepts";	
	}
	
	
}
