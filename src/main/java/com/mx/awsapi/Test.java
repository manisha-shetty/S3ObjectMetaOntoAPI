package com.mx.awsapi;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import org.apache.xerces.util.URI.MalformedURIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.mx.awsapi.dao.S3ObjectDao;
import com.mx.awsapi.model.DocumentObjectMetadata;
import com.mx.awsapi.model.VideoObjectMetadata;
import com.mx.awsapi.service.S3ObjectService;



public class Test {


	public static void main(String[] args) throws IOException, MalformedURLException, ParseException{

		ApplicationContext context = 
				new ClassPathXmlApplicationContext("applicationContext.xml");		    	
		S3ObjectService s3ObjectService = (S3ObjectService) context.getBean("s3ObjectService");

		AWSCredentials awsCredentials = new BasicAWSCredentials("AKIAJPGDNP3TXESMJTOQ","W/Nnf6xF35JA6UmJV0JLnbtrze6u4R10mkXh4JGt");
		AmazonS3Client s3Client = new AmazonS3Client(awsCredentials);


		DateFormat df = new SimpleDateFormat("MM/dd/yyyy"); 

		DocumentObjectMetadata dom = new DocumentObjectMetadata();
		dom.setBibliographicCitation("citation 1");
		dom.setBucketName("document-bucket");
		dom.setCacheControl("test cache control");
		dom.setContentDisposition("test content disposition");
		dom.setContentEncoding("test encoding");
		dom.setContentLength(100);
		dom.setContentMD5("MD5");
		dom.setContentType("doc");
		dom.setCreated(df.parse("07/02/2016"));
		dom.setCreator("Manisha");
		dom.setDescription("test descirption");
		dom.setExpirationTime(df.parse("07/02/2020"));
		dom.setExpirationTimeRuleId("test rule id");
		dom.setExtent(50);
		dom.setFormat("pdf");
		dom.setHttpExpiresDate(df.parse("07/02/2020"));
		dom.setKeyName("Preventing Information Inference in Access Control");
		dom.setLanguage("English");
		dom.setLastModified(df.parse("07/02/2016"));
		dom.setObjectFilePath("/media/manisha/863886A2388690BB/Work/VJTI/VJTI MTech Project/Papers/ACM/Preventing Information Inference in Access Control.pdf");
		dom.setOngoingRestore(false);
		dom.setPublisher("IEEE");
		dom.setRestoreExpirationTime(df.parse("07/02/2016"));
		dom.setSSEAlgorithm("SSE algo");
		dom.setSSECustomerAlgorithm("SSE Customer");
		dom.setSSECustomerKeyMd5("MD5 digest");
		dom.setTitle("Preventing Information Inference in Access Control");
		dom.setUrl("");

		s3ObjectService.uploadDocumentObject(dom, s3Client);

		//s3ObjectService.searchVideoByLanguage("English",s3Client);

	}


}

