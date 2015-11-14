package com.mx.awsapi.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.mx.awsapi.dao.OntologyDao;
import com.mx.awsapi.model.S3CustomObjectMetadata;
import com.mx.awsapi.model.VideoObjectMetadata;

public class OntologyService {

	static Logger log = Logger.getLogger(OntologyService.class.getName());

	@Autowired
	private OntologyDao ontologyDao;

	private String owlFilePath;

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

	
	public String getOwlFilePath() {
		return owlFilePath;
	}

	public void setOwlFilePath(String owlFilePath) {
		this.owlFilePath=owlFilePath;
		File owlFile=new File(owlFilePath);
		try {
			FileReader owlFileReader = new FileReader(owlFile);
			model=ModelFactory.createOntologyModel();
			model.read(owlFileReader, null);



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

	public Individual addS3ObjectMetadata(S3CustomObjectMetadata com, Individual ind){

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

	public Individual addS3CustomObjectMetadata(S3CustomObjectMetadata com, Individual ind){

		ind.addProperty(model.getProperty(getNs()+"key_name"),(com.getKeyName()==null)?"":com.getKeyName());
		ind.addProperty(model.getProperty(getNs()+"bucket_name"),(com.getBucketName()==null)?"":com.getBucketName());
		ind.addProperty(model.getProperty(getNs()+"object_file_path"),(com.getObjectFilePath()==null)?"":com.getObjectFilePath());
		ind.addLiteral(model.getProperty(getNs()+"size_in_kb"),com.getSizeInKB());
		return ind;
	}
	
	public void createVideoIndividual(VideoObjectMetadata vom){
		System.out.println("OntologyService - Creating VideoObjectMetadata Individual for "+ vom.getKeyName());
		OntClass vomClass= model.getOntClass(getNs()+"VideoObjectMetadata");
		Individual vomInd=vomClass.createIndividual(getNs()+"VOM_"+vom.getKeyName());
		//set User metadata
		vomInd=addS3ObjectMetadata(vom, vomInd);
		vomInd=addS3CustomObjectMetadata(vom, vomInd);

		vomInd.addProperty(model.getProperty(getNs()+"format"),vom.getFormat());
		vomInd.addProperty(model.getProperty(getNs()+"caption"),vom.getCaption());
		vomInd.addProperty(model.getProperty(getNs()+"language"),vom.getLanguage());
		vomInd.addLiteral(model.getProperty(getNs()+"length_in_seconds"),vom.getLengthInSeconds());
		try {
			stream= new PrintStream(getOwlFilePath());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		model.write(getStream(), "RDF/XML",null);

	}

	public List<Map<String,String>> searchVideoByLanguage(String language){

		List<Map<String,String>> mapList = new ArrayList<Map<String,String>>();
		String queryString =
				"prefix ns: <"+getNs() +">" +
						"prefix vom: <"+getNs() +"VideoObjectMetadata> " +       
						"prefix rdfs: <" + RDFS.getURI() + "> "           +
						"prefix owl: <" + OWL.getURI() + "> "             +
						"select ?key ?bucket where {?i a ns:VideoObjectMetadata ." +
						"?i ns:key_name ?key ." +
						"?i ns:bucket_name ?bucket" +
						"?i ns:language '"+ language +"'}";

		Query query = QueryFactory.create(queryString);
		QueryExecution qe = QueryExecutionFactory.create(query, getModel());
		com.hp.hpl.jena.query.ResultSet results =  qe.execSelect();
		while(results.hasNext()){
			QuerySolution qs=results.next();
			Map<String,String> map=new HashMap<String,String>();
			//System.out.println(qs.getLiteral("key") +" "+qs.getLiteral("bucket"));
			map.put("keyName",qs.getLiteral("key").toString());
			map.put("bucketName",qs.getLiteral("bucket").toString());
			mapList.add(map);
		}
		//ResultSetFormatter.out(System.out, results, query);
		qe.close();
		return mapList;
	}

}
