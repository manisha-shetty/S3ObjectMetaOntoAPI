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
import com.mx.awsapi.model.S3CustomObjectMetadata;
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

	public ObjectMetadata addSystemMetadata(S3CustomObjectMetadata com, ObjectMetadata objectMetadata){
		//set all Amazon S3 system metadata
		objectMetadata.setCacheControl(com.getCacheControl());
		objectMetadata.setContentDisposition(com.getContentDisposition());
		objectMetadata.setContentEncoding(com.getContentEncoding());
		objectMetadata.setContentLength(com.getContentLength());
		objectMetadata.setContentMD5(com.getContentMD5());
		objectMetadata.setContentType(com.getContentType());
		objectMetadata.setExpirationTime(com.getExpirationTime());
		objectMetadata.setExpirationTimeRuleId(com.getExpirationTimeRuleId());
		objectMetadata.setHttpExpiresDate(com.getHttpExpiresDate());
		objectMetadata.setLastModified(com.getLastModified());
		objectMetadata.setOngoingRestore((com.getOngoingRestore()==null)?false:com.getOngoingRestore());
		objectMetadata.setRestoreExpirationTime(com.getRestoreExpirationTime());
		objectMetadata.setSSEAlgorithm(com.getSSEAlgorithm());
		objectMetadata.setSSECustomerAlgorithm(com.getSSECustomerAlgorithm());
		objectMetadata.setSSECustomerKeyMd5(com.getSSECustomerKeyMd5());
		return objectMetadata;
	}
	public void uploadVideoObject(VideoObjectMetadata vom, AmazonS3Client s3Client, String bucketName){
		try {
			System.out.println("S3ObjectService - Preparing Upload for"+ vom.getKeyName());

			ontologyService.createVOMIndividual(vom);

			FileInputStream stream = new FileInputStream(vom.getObjectFilePath());

			System.out.println("S3ObjectService - Creating S3 Object Metadata for "+ vom.getKeyName());
			ObjectMetadata objectMetadata=new ObjectMetadata();
			objectMetadata=addSystemMetadata(vom, objectMetadata);
			objectMetadata.addUserMetadata("format", vom.getFormat());
			objectMetadata.addUserMetadata("caption", vom.getCaption());
			objectMetadata.addUserMetadata("language", vom.getLanguage());
			objectMetadata.addUserMetadata("lengthInSeconds", String.valueOf(vom.getLengthInSeconds()));

			//PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName+"/", vom.getKeyName(), stream, objectMetadata);
			//PutObjectResult result = s3Client.putObject(putObjectRequest);
			//System.out.println("Etag:" + result.getETag() + "-->" + result);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
