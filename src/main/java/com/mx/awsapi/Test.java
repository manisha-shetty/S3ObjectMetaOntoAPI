package com.mx.awsapi;

import java.io.File;
import java.io.FileReader;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

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
import com.mx.awsapi.model.VideoObjectMetadata;
import com.mx.awsapi.service.S3ObjectService;



public class Test {
	
	
	public static void main(String[] args){
		
		ApplicationContext context = 
		    	  new ClassPathXmlApplicationContext("applicationContext.xml");		    	
		    	S3ObjectService s3ObjectService = (S3ObjectService) context.getBean("s3ObjectService");
		    	
		    	AWSCredentials awsCredentials = new BasicAWSCredentials("key","key");
				AmazonS3Client s3Client = new AmazonS3Client(awsCredentials);

		    VideoObjectMetadata vom=new VideoObjectMetadata();
		    vom.setFormat("JPG");
		    vom.setDurationInSeconds(120);
		    vom.setKeyName("Capture.JPG");
		    vom.setBucketName("test-bucket-manisha");
		    vom.setObjectFilePath("/home/manisha/Downloads/Capture.JPG");
		    vom.setLanguage("English");
		    vom.setTitle("Capture");
		    vom.setContentLength(0);
		    vom.setContentType("image/jpeg");
		   // s3ObjectService.uploadVideoObject(vom, s3Client);
		    

		   s3ObjectService.searchVideoByLanguage("English",s3Client);
		    
		}
	

	}

