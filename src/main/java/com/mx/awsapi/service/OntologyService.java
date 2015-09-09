package com.mx.awsapi.service;

import org.springframework.beans.factory.annotation.Autowired;

import com.mx.awsapi.dao.OntologyDao;

public class OntologyService {

	@Autowired
	private OntologyDao ontologyDao;

	public OntologyDao getOntologyDao() {
		return ontologyDao;
	}

	public void setOntologyDao(OntologyDao ontologyDao) {
		this.ontologyDao = ontologyDao;
	}
	
	
}
