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
		    	
		    	
		    	AWSCredentials awsCredentials = new BasicAWSCredentials("access","sec");
				AmazonS3Client s3Client = new AmazonS3Client(awsCredentials);
			
				String bucketName="test-bucket-manisha";
		    VideoObjectMetadata vom=new VideoObjectMetadata();
		    vom.setFormat("mp4");
		    vom.setLengthInSeconds(120);
		    vom.setKeyName("Silicon.Valley.S01E01.HDTV.x264-KILLERS.mp4");
		    vom.setObjectFilePath("/home/manisha/Downloads/Capture.JPG");
		    vom.setLanguage("English");
		    vom.setCaption("Silicon.Valley.S01E01.HDTV.x264-KILLERS");
		    vom.setCacheControl("");
		    vom.setContentDisposition("");
		    vom.setContentEncoding("");
		    vom.setContentLength(0);
		    vom.setContentMD5("");
		    vom.setExpirationTime(new Date());
		    vom.setExpirationTimeRuleId("");
		    s3ObjectService.uploadVideoObject(vom, s3Client, bucketName);
		    
		    
//		try{
//		String pathName="/home/manisha/ManishaOntologies/S3ObjectMetaOnto/S3ObjectMetaOnto.owl";
//		File owlFile=new File(pathName);
//		FileReader owlFileReader=new FileReader(owlFile);
//		OntModel model=ModelFactory.createOntologyModel();
//		model.read(owlFileReader, null);
//		Iterator classIter=model.listClasses();
//		while(classIter.hasNext()){
//			OntClass ontClass=(OntClass)classIter.next();
//			System.out.println(ontClass.getURI());
//		}
		
	
//		
		
		//s3Client.createBucket(bucketName);
		
//		
//	    for (Bucket bucket : s3Client.listBuckets()) {
//	    	System.out.println(" - " + bucket.getName());
//	    }
//	    
		
		
    
//	    
//	    
//		}catch(Exception e){
//			
//			System.out.println("message:"+e);
//		}	
//			
		
		
		
		
		}
	

	}

