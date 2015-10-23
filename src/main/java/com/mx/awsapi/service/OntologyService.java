package com.mx.awsapi.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintStream;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.mx.awsapi.dao.OntologyDao;
import com.mx.awsapi.model.S3CustomObjectMetadata;
import com.mx.awsapi.model.VideoObjectMetadata;

public class OntologyService {

	static Logger log = Logger.getLogger(OntologyService.class.getName());

	@Autowired
	private OntologyDao ontologyDao;

	private OntModel model;

	private String ns;

	private PrintStream stream;

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
			stream= new PrintStream(owlFilePath);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	public String getNs() {
		return ns;
	}

	public void setNs(String ns) {
		this.ns = ns;
	}

	public PrintStream getStream() {
		return stream;
	}

	public void setStream(PrintStream stream) {
		this.stream = stream;
	}

	public Individual addSystemMetadataToIndividual(S3CustomObjectMetadata com, Individual ind){

		ind.addProperty(model.getProperty(getNs()+"cache_control"),(com.getCacheControl()==null)?"":com.getCacheControl());
		ind.addProperty(model.getProperty(getNs()+"content_disposition"),(com.getContentDisposition()==null)?"":com.getContentDisposition());
		ind.addProperty(model.getProperty(getNs()+"content_encoding"),(com.getContentEncoding()==null)?"":com.getContentEncoding());
		ind.addLiteral(model.getProperty(getNs()+"content_length"),com.getContentLength());
		ind.addProperty(model.getProperty(getNs()+"content_md5"),(com.getContentMD5()==null)?"":com.getContentMD5());
		ind.addProperty(model.getProperty(getNs()+"content_type"),(com.getContentType()==null)?"":com.getContentType());
		ind.addLiteral(model.getProperty(getNs()+"expiration_time"),(com.getExpirationTime()==null)?"":com.getExpirationTime());
		ind.addProperty(model.getProperty(getNs()+"expiration_time_rule_id"),(com.getExpirationTimeRuleId()==null)?"":com.getExpirationTimeRuleId());
		ind.addLiteral(model.getProperty(getNs()+"http_expires_date"),(com.getHttpExpiresDate()==null)?"":com.getHttpExpiresDate());
		ind.addLiteral(model.getProperty(getNs()+"last_modified"),(com.getLastModified()==null)?"":com.getLastModified());
		ind.addLiteral(model.getProperty(getNs()+"ongoing_restore"),(com.getOngoingRestore()==null)?"":com.getOngoingRestore());
		ind.addLiteral(model.getProperty(getNs()+"restore_expiration_time"),(com.getRestoreExpirationTime()==null)?"":com.getRestoreExpirationTime());
		ind.addProperty(model.getProperty(getNs()+"sse_algorithm"),(com.getSSEAlgorithm()==null)?"":com.getSSEAlgorithm());
		ind.addProperty(model.getProperty(getNs()+"sse_customer_algorithm"),(com.getSSECustomerAlgorithm()==null)?"":com.getSSECustomerAlgorithm());
		ind.addProperty(model.getProperty(getNs()+"sse_customer_key_md5"),(com.getSSECustomerKeyMd5()==null)?"":com.getSSECustomerKeyMd5());
		return ind;

	}
	public void createVOMIndividual(VideoObjectMetadata vom){


		System.out.println("OntologyService - Creating VideoObjectMetadata Individual for "+ vom.getKeyName());
		//	model.createClass(getNs()+"AudioObjectMetadata");
		//model.createIndividual(getNs()+"VOM_"+vom.getKeyName(), vomClass);

		/*for (  ExtendedIterator<? extends OntResource> vomInds = vomClass.listInstances(); vomInds.hasNext();) {
			System.out.println( vomInds.next() ); 	 

		}*/

		OntClass vomClass= model.getOntClass(getNs()+"VideoObjectMetadata");
		Individual vomInd=vomClass.createIndividual(getNs()+"VOM_"+vom.getKeyName());
		//set User metadata
		vomInd=addSystemMetadataToIndividual(vom, vomInd);
		vomInd.addProperty(model.getProperty(getNs()+"format"),vom.getFormat());
		vomInd.addProperty(model.getProperty(getNs()+"caption"),vom.getCaption());
		vomInd.addProperty(model.getProperty(getNs()+"language"),vom.getLanguage());
		vomInd.addLiteral(model.getProperty(getNs()+"length_in_seconds"),vom.getLengthInSeconds());
		model.write(getStream(), "RDF/XML",null);

	}

}
