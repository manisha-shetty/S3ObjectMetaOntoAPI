package com.mx.awsapi.service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.xerces.util.URI.MalformedURIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.amazonaws.services.s3.AmazonS3Client;
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
import com.mx.awsapi.model.AudioObjectMetadata;
import com.mx.awsapi.model.DocumentObjectMetadata;
import com.mx.awsapi.model.ImageObjectMetadata;
import com.mx.awsapi.model.S3CustomObjectMetadata;
import com.mx.awsapi.model.VideoObjectMetadata;

@Component
public class OntologyService {

	static Logger log = Logger.getLogger(OntologyService.class.getName());

	@Autowired
	private Properties ontologyKeys;

	@Autowired
	private OntologyDao ontologyDao;

	private String owlFilePath;

	private OntModel model;

	private String ns;

	private String dc;

	private PrintStream stream;
	//private BufferedOutputStream stream;

	DateFormat df = new SimpleDateFormat("MM/dd/yyyy"); 

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

	public void setOwlFilePath(String owlFilePath)   throws  IOException {
		this.owlFilePath=owlFilePath;
		File owlFile=new File(owlFilePath);
		try {
			FileReader owlFileReader = new FileReader(owlFile);
			model=ModelFactory.createOntologyModel();
			model.read(owlFileReader, null);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		/* BufferedInputStream in = null;
	    try {
	        in = new BufferedInputStream(new URL("http://manisha.netii.net/S3ObjectMetaOnto.owl").openStream());
	        model=ModelFactory.createOntologyModel();
			model.read(in, null);
	    } finally {
	        if (in != null) {
	            in.close();
	        }
	    }*/
	}

	public String getNs() {
		return ns;
	}

	public void setNs(String ns) {
		this.ns = ns;
	}


	public String getDc() {
		return dc;
	}

	public void setDc(String dc) {
		this.dc = dc;
	}

	public PrintStream getStream() {
		return stream;
	}

	public void setStream(PrintStream stream) {
		this.stream = stream;
	}

	/*	public BufferedOutputStream getStream() {
		return stream;
	}

	public void setStream(BufferedOutputStream stream) {
		this.stream = stream;
	}*/

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

		ind.addProperty(model.getProperty(getDc()+"identifier"),(com.getBucketName()==null?"":com.getBucketName())+""+(com.getKeyName()==null?"":com.getKeyName()));
		ind.addProperty(model.getProperty(getNs()+"bucket_name"),(com.getBucketName()==null?"":com.getBucketName()));
		ind.addProperty(model.getProperty(getDc()+"created"),(com.getCreated()==null)?"":df.format(com.getCreated()));
		ind.addProperty(model.getProperty(getDc()+"creator"),(com.getCreator()==null)?"":com.getCreator());
		ind.addProperty(model.getProperty(getDc()+"description"),(com.getDescription()==null)?"":com.getDescription());
		ind.addLiteral(model.getProperty(getDc()+"extent"),com.getExtent());
		ind.addProperty(model.getProperty(getDc()+"format"),(com.getFormat()==null)?"":com.getFormat());
		ind.addProperty(model.getProperty(getNs()+"key_name"),(com.getKeyName()==null?"":com.getKeyName()));
		ind.addProperty(model.getProperty(getNs()+"object_file_path"),(com.getObjectFilePath()==null)?"":com.getObjectFilePath());
		ind.addProperty(model.getProperty(getDc()+"title"),(com.getTitle()==null)?"":com.getTitle());
		ind.addLiteral(model.getProperty(getNs()+"url"),com.getUrl());
		return ind;
	}


	public void createAudioIndividual(AudioObjectMetadata aom) throws IOException, MalformedURLException{
		System.out.println("OntologyService - Creating AudioObjectMetadata Individual for "+ aom.getKeyName());
		OntClass aomClass= model.getOntClass(getNs()+"AudioObjectMetadata");
		Individual aomInd=aomClass.createIndividual(getNs()+"AOM_"+aom.getKeyName());
		//set User metadata
		aomInd=addS3ObjectMetadata(aom, aomInd);
		aomInd=addS3CustomObjectMetadata(aom, aomInd);
		aomInd.addProperty(model.getProperty(getNs()+"album"),aom.getAlbum());
		aomInd.addProperty(model.getProperty(getNs()+"encoder"),aom.getEncoder());
		aomInd.addProperty(model.getProperty(getDc()+"language"),aom.getLanguage());
		aomInd.addLiteral(model.getProperty(getNs()+"lengthInSeconds"),aom.getLengthInSeconds());
		aomInd.addLiteral(model.getProperty(getNs()+"trackNumber"),aom.getTrackNumber());	
		aomInd.addProperty(model.getProperty(getDc()+"type"), aom.getType());
		/*try {
				URLConnection urlc=new URL("http://manisha.netii.net/S3ObjectMetaOnto.owl").openConnection();
				urlc.setDoOutput(true);
				urlc.setAllowUserInteraction(true);
				stream= new BufferedOutputStream(urlc.getOutputStream());
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
		try {
			stream= new PrintStream(getOwlFilePath());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		model.write(getStream(), "RDF/XML",null);

	}

	public void createDocumentIndividual(DocumentObjectMetadata dom) throws IOException, MalformedURLException{
		System.out.println("OntologyService - Creating DocumentObjectMetadata Individual for "+ dom.getKeyName());
		OntClass domClass= model.getOntClass(getNs()+"DocumentObjectMetadata");
		Individual domInd=domClass.createIndividual(getNs()+"DOM_"+dom.getKeyName());
		//set User metadata
		domInd=addS3ObjectMetadata(dom, domInd);
		domInd=addS3CustomObjectMetadata(dom, domInd);

		domInd.addProperty(model.getProperty(getDc()+"language"),dom.getFormat());
		domInd.addProperty(model.getProperty(getDc()+"publisher"),dom.getTitle());
		domInd.addProperty(model.getProperty(getDc()+"bibliographicCitation"),dom.getBibliographicCitation());
		/*try {
				URLConnection urlc=new URL("http://manisha.netii.net/S3ObjectMetaOnto.owl").openConnection();
				urlc.setDoOutput(true);
				urlc.setAllowUserInteraction(true);
				stream= new BufferedOutputStream(urlc.getOutputStream());
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
		try {
			stream= new PrintStream(getOwlFilePath());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		model.write(getStream(), "RDF/XML",null);

	}

	public void createImageIndividual(ImageObjectMetadata iom) throws IOException, MalformedURLException{
		System.out.println("OntologyService - Creating ImageObjectMetadata Individual for "+ iom.getKeyName());
		OntClass iomClass= model.getOntClass(getNs()+"ImageObjectMetadata");
		Individual iomInd=iomClass.createIndividual(getNs()+"IOM_"+iom.getKeyName());
		//set User metadata
		iomInd=addS3ObjectMetadata(iom, iomInd);
		iomInd=addS3CustomObjectMetadata(iom, iomInd); 
		iomInd.addProperty(model.getProperty(getNs()+"colorSpace"),iom.getColorSpace());
		iomInd.addLiteral(model.getProperty(getNs()+"imageHeight"),iom.getImageHeight());
		iomInd.addLiteral(model.getProperty(getNs()+"imageWidth"),iom.getImageWidth());
		iomInd.addLiteral(model.getProperty(getNs()+"location"),iom.getLocation());	
		iomInd.addProperty(model.getProperty(getNs()+"orientation"), iom.getOrientation());
		iomInd.addProperty(model.getProperty(getNs()+"pixels"),df.format(iom.getPixels()));
		iomInd.addProperty(model.getProperty(getDc()+"subject"), iom.getSubject());
		/*try {
			URLConnection urlc=new URL("http://manisha.netii.net/S3ObjectMetaOnto.owl").openConnection();
			urlc.setDoOutput(true);
			urlc.setAllowUserInteraction(true);
			stream= new BufferedOutputStream(urlc.getOutputStream());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		try {
			stream= new PrintStream(getOwlFilePath());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		model.write(getStream(), "RDF/XML",null);
	}

	public void createVideoIndividual(VideoObjectMetadata vom) throws IOException, MalformedURLException{
		System.out.println("OntologyService - Creating VideoObjectMetadata Individual for "+ vom.getKeyName());
		OntClass vomClass= model.getOntClass(getNs()+"VideoObjectMetadata");
		Individual vomInd=vomClass.createIndividual(getNs()+"VOM_"+vom.getKeyName());
		//set User metadata
		vomInd=addS3ObjectMetadata(vom, vomInd);
		vomInd=addS3CustomObjectMetadata(vom, vomInd);
		vomInd.addProperty(model.getProperty(getDc()+"language"),vom.getLanguage());
		vomInd.addLiteral(model.getProperty(getNs()+"length_in_seconds"),vom.getLengthInSeconds());	
		vomInd.addProperty(model.getProperty(getDc()+"subject"), vom.getSubject());
		vomInd.addProperty(model.getProperty(getDc()+"type"), vom.getType());
		/*try {
			URLConnection urlc=new URL("http://manisha.netii.net/S3ObjectMetaOnto.owl").openConnection();
			urlc.setDoOutput(true);
			urlc.setAllowUserInteraction(true);
			stream= new BufferedOutputStream(urlc.getOutputStream());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		try {
			stream= new PrintStream(getOwlFilePath());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		model.write(getStream(), "RDF/XML",null);
	}


	/********************************SEARCH AUDIO OJECTS***************************/

	public List<Map<String,String>> searchAudioByMetadata(String key, String value){
		List<Map<String,String>> mapList = new ArrayList<Map<String,String>>();
		String queryString =
				"prefix ns: <"+getNs() +"> " +
						"prefix aom: <"+getNs() +"AudioObjectMetadata> " +       
						"prefix rdfs: <" + RDFS.getURI() + "> "           +
						"prefix owl: <" + OWL.getURI() + "> "             +
						"select ?key ?bucket where {?i a ns:AudioObjectMetadata ." +
						"?i ns:key_name ?key ." +
						"?i ns:bucket_name ?bucket ." +
						"?i ns:"+key+" '"+ value +"'}";
		System.out.println(queryString);
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

	public List<Map<String,String>> searchAudioByCreator(String creator){
		return searchAudioByMetadata(ontologyKeys.getProperty("somKey.creator"),creator);
	}

	public List<Map<String,String>> searchAudioByDescription(String description){
		return searchAudioByMetadata(ontologyKeys.getProperty("somkey.description"),description);
	}

	public List<Map<String,String>> searchAudioByFormat(String format){
		return searchAudioByMetadata(ontologyKeys.getProperty("somkey.format"),format);
	}

	public List<Map<String,String>> searchAudioByTitle(String title){
		return searchAudioByMetadata(ontologyKeys.getProperty("somkey.title"),title);
	}

	public List<Map<String,String>> searchAudioByAlbum(String album){
		return searchAudioByMetadata(ontologyKeys.getProperty("aomkey.album"),album);
	}

	public List<Map<String,String>> searchAudioByEncoder(String encoder){
		return searchAudioByMetadata(ontologyKeys.getProperty("aomkey.encoder"),encoder);
	}

	public List<Map<String,String>> searchAudioBylanguage(String language){
		return searchAudioByMetadata(ontologyKeys.getProperty("aomkey.language"),language);
	}

	public List<Map<String,String>> searchAudioByType(String type){
		return searchAudioByMetadata(ontologyKeys.getProperty("aomkey.type"),type);
	}


	/********************************SEARCH DOCUMENT OJECTS***************************/

	public List<Map<String,String>> searchDocumentByMetadata(String key, String value){
		List<Map<String,String>> mapList = new ArrayList<Map<String,String>>();
		String queryString =
				"prefix ns: <"+getNs() +"> " +
						"prefix dom: <"+getNs() +"DocumentObjectMetadata> " +       
						"prefix rdfs: <" + RDFS.getURI() + "> "           +
						"prefix owl: <" + OWL.getURI() + "> "             +
						"select ?key ?bucket where {?i a ns:DocumentObjectMetadata ." +
						"?i ns:key_name ?key ." +
						"?i ns:bucket_name ?bucket ." +
						"?i ns:"+key+" '"+ value +"'}";
		System.out.println(queryString);
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

	public List<Map<String,String>> searchDocumentByCreator(String creator){
		return searchDocumentByMetadata(ontologyKeys.getProperty("somKey.creator"),creator);
	}

	public List<Map<String,String>> searchDocumentByDescription(String description){
		return searchDocumentByMetadata(ontologyKeys.getProperty("somkey.description"),description);
	}

	public List<Map<String,String>> searchDocumentByFormat(String format){
		return searchDocumentByMetadata(ontologyKeys.getProperty("somkey.format"),format);
	}

	public List<Map<String,String>> searchDocumentByTitle(String title){
		return searchDocumentByMetadata(ontologyKeys.getProperty("somkey.title"),title);
	}

	public List<Map<String,String>> searchDocumentByLanguage(String language){
		return searchDocumentByMetadata(ontologyKeys.getProperty("domkey.language"),language);
	}

	public List<Map<String,String>> searchDocumentByPublisher(String publisher){
		return searchDocumentByMetadata(ontologyKeys.getProperty("domkey.publisher"),publisher);
	}

	public List<Map<String,String>> searchDocumentByBibliographicCitation(String bibliographicCitation){
		return searchDocumentByMetadata(ontologyKeys.getProperty("domkey.bibliographicCitation"),bibliographicCitation);
	}

	/********************************SEARCH IMAGE OJECTS***************************/

	public List<Map<String,String>> searchImageByMetadata(String key, String value){
		List<Map<String,String>> mapList = new ArrayList<Map<String,String>>();
		String queryString =
				"prefix ns: <"+getNs() +"> " +
						"prefix iom: <"+getNs() +"ImageObjectMetadata> " +       
						"prefix rdfs: <" + RDFS.getURI() + "> "           +
						"prefix owl: <" + OWL.getURI() + "> "             +
						"select ?key ?bucket where {?i a ns:ImageObjectMetadata ." +
						"?i ns:key_name ?key ." +
						"?i ns:bucket_name ?bucket ." +
						"?i ns:"+key+" '"+ value +"'}";
		System.out.println(queryString);
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

	public List<Map<String,String>> searchImageByCreator(String creator){
		return searchImageByMetadata(ontologyKeys.getProperty("somKey.creator"),creator);
	}

	public List<Map<String,String>> searchImageByDescription(String description){
		return searchImageByMetadata(ontologyKeys.getProperty("somkey.description"),description);
	}

	public List<Map<String,String>> searchImageByFormat(String format){
		return searchImageByMetadata(ontologyKeys.getProperty("somkey.format"),format);
	}

	public List<Map<String,String>> searchImageByTitle(String title){
		return searchImageByMetadata(ontologyKeys.getProperty("somkey.title"),title);
	}

	public List<Map<String,String>> searchImageByColorSpace(String colorSpace){
		return searchImageByMetadata(ontologyKeys.getProperty("iomkey.colorSpace"),colorSpace);
	}

	public List<Map<String,String>> searchImageByLocation(String location){
		return searchImageByMetadata(ontologyKeys.getProperty("iomkey.location"),location);
	}

	public List<Map<String,String>> searchImageBySubject(String subject){
		return searchImageByMetadata(ontologyKeys.getProperty("iomkey.subject"),subject);
	}

	/********************************SEARCH VIDEO OJECTS***************************/

	public List<Map<String,String>> searchVideoByMetadata(String key, String value){
		List<Map<String,String>> mapList = new ArrayList<Map<String,String>>();
		String queryString =
				"prefix ns: <"+getNs() +"> " +
						"prefix vom: <"+getNs() +"VideoObjectMetadata> " +       
						"prefix rdfs: <" + RDFS.getURI() + "> "           +
						"prefix owl: <" + OWL.getURI() + "> "             +
						"select ?key ?bucket where {?i a ns:VideoObjectMetadata ." +
						"?i ns:key_name ?key ." +
						"?i ns:bucket_name ?bucket ." +
						"?i ns:"+key+" '"+ value +"'}";
		System.out.println(queryString);
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

	public List<Map<String,String>> searchVideoByCreator(String creator){
		return searchVideoByMetadata(ontologyKeys.getProperty("somKey.creator"),creator);
	}

	public List<Map<String,String>> searchVideoByDescription(String description){
		return searchVideoByMetadata(ontologyKeys.getProperty("somkey.description"),description);
	}

	public List<Map<String,String>> searchVideoByFormat(String format){
		return searchVideoByMetadata(ontologyKeys.getProperty("somkey.format"),format);
	}

	public List<Map<String,String>> searchVideoByTitle(String title){
		return searchVideoByMetadata(ontologyKeys.getProperty("somkey.title"),title);
	}

	public List<Map<String,String>> searchVideoByLanguage(String language){
		return searchVideoByMetadata(ontologyKeys.getProperty("vomKey.language"),language);
	}

	public List<Map<String,String>> searchVideoBySubject(String subject){
		return searchVideoByMetadata(ontologyKeys.getProperty("vomKey.subject"),subject);
	}

	public List<Map<String,String>> searchVideoByType(String type){
		return searchVideoByMetadata(ontologyKeys.getProperty("vomKey.type"),type);
	}



}
