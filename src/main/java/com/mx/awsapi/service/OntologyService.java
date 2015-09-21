package com.mx.awsapi.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.mx.awsapi.dao.OntologyDao;
import com.mx.awsapi.model.VideoObjectMetadata;

public class OntologyService {

	static Logger log = Logger.getLogger(OntologyService.class.getName());

	@Autowired
	private OntologyDao ontologyDao;

	private OntModel model;

	public OntologyDao getOntologyDao() {
		return ontologyDao;
	}

	public void setOntologyDao(OntologyDao ontologyDao) {
		this.ontologyDao = ontologyDao;
	}

	public OntModel getModel() {
		return model;
	}

	public void setModel(String owlFilePath) {
		File owlFile=new File(owlFilePath);
		try {
			FileReader owlFileReader = new FileReader(owlFile);
			model=ModelFactory.createOntologyModel();
			model.read(owlFileReader, null);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	public void createVOMIndividual(VideoObjectMetadata vom){
		log.info("Creating VOM individual");
	}

}
