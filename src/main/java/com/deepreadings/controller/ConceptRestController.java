package com.deepreadings.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.deepreadings.model.Concept;
import com.deepreadings.service.ConceptService;

@RestController
@RequestMapping("rest/concepts")
public class ConceptRestController {

	private static final Logger logger = LoggerFactory.getLogger(ConceptRestController.class);
	
	@Autowired
	ConceptService conceptService;
	
	
	@RequestMapping(method=RequestMethod.GET)
	@ResponseBody
	public List<Concept> readConcepts() {
		List<Concept> conceptList = conceptService.readAll();
		logger.info("##### ConceptList: {}", conceptList);
		return conceptList;
	}

	
}
