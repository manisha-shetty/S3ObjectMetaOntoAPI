package com.mx.awsapi.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.mx.awsapi.dao.S3ObjectDao;
import com.mx.awsapi.model.VideoObjectMetadata;


public class S3ObjectService {

	//static Logger log = Logger.getLogger(S3ObjectService.class.getName());

	@Autowired
	private S3ObjectDao s3ObjectDao;

	@Autowired
	private OntologyService ontologyService;


	public S3ObjectDao getS3ObjectDao() {
		return s3ObjectDao;
	}

	public void setS3ObjectDao(S3ObjectDao s3ObjectDao) {
		this.s3ObjectDao = s3ObjectDao;
	}

	public void uploadVideoObject(VideoObjectMetadata vom, AmazonS3Client s3Client, String bucketName){
		try {
			//ontologyService.createVOMIndividual(vom);

			FileInputStream stream = new FileInputStream(vom.getObjectFilePath());

			ObjectMetadata objectMetadata = new ObjectMetadata();
			objectMetadata.setCacheControl(vom.getCacheControl());
			objectMetadata.addUserMetadata("format", vom.getFormat());
			objectMetadata.addUserMetadata("caption", vom.getCaption());
			objectMetadata.addUserMetadata("language", vom.getLanguage());
			objectMetadata.addUserMetadata("lengthInSeconds", String.valueOf(vom.getLengthInSeconds()));

			PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName+"/", vom.getKeyName(), stream, objectMetadata);
			PutObjectResult result = s3Client.putObject(putObjectRequest);
			System.out.println("Etag:" + result.getETag() + "-->" + result);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
