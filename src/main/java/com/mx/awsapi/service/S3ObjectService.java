package com.mx.awsapi.service;


import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
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

	public ObjectMetadata setS3ObjectMetadataForUpload(S3CustomObjectMetadata com, ObjectMetadata objectMetadata){
		//set all Amazon S3 system metadata
		if(com.getCacheControl()!=null) objectMetadata.setCacheControl(com.getCacheControl());
		if(com.getContentDisposition()!=null)objectMetadata.setContentDisposition(com.getContentDisposition());
		if(com.getContentEncoding()!=null) objectMetadata.setContentEncoding(com.getContentEncoding());
		objectMetadata.setContentLength(com.getContentLength());
		if(com.getContentMD5()!=null) objectMetadata.setContentMD5(com.getContentMD5());
		if(com.getContentType()!=null) objectMetadata.setContentType(com.getContentType());
		if(com.getExpirationTime()!=null) objectMetadata.setExpirationTime(com.getExpirationTime());
		if(com.getExpirationTimeRuleId()!=null)objectMetadata.setExpirationTimeRuleId(com.getExpirationTimeRuleId());
		if(com.getHttpExpiresDate()!=null) objectMetadata.setHttpExpiresDate(com.getHttpExpiresDate());
		if(com.getLastModified()!=null) objectMetadata.setLastModified(com.getLastModified());
		if(com.getOngoingRestore()!=null) objectMetadata.setOngoingRestore(com.getOngoingRestore());
		if(com.getObjectFilePath()!=null) objectMetadata.setRestoreExpirationTime(com.getRestoreExpirationTime());
		if(com.getSSEAlgorithm()!=null) objectMetadata.setSSEAlgorithm(com.getSSEAlgorithm());
		if(com.getSSECustomerAlgorithm()!=null) objectMetadata.setSSECustomerAlgorithm(com.getSSECustomerAlgorithm());
		if(com.getSSECustomerKeyMd5()!=null) objectMetadata.setSSECustomerKeyMd5(com.getSSECustomerKeyMd5());
		return objectMetadata;
	}

	public S3CustomObjectMetadata setS3ObjectMetadataAfterDownload(S3CustomObjectMetadata com, ObjectMetadata objectMetadata){
		com.setCacheControl(objectMetadata.getCacheControl());
		com.setContentDisposition(objectMetadata.getContentDisposition());
		com.setContentEncoding(objectMetadata.getContentEncoding());
		com.setContentLength(objectMetadata.getContentLength());
		com.setContentMD5(objectMetadata.getContentMD5());
		com.setContentType(objectMetadata.getContentType());
		com.setExpirationTime(objectMetadata.getExpirationTime());
		com.setExpirationTimeRuleId(objectMetadata.getExpirationTimeRuleId());
		com.setHttpExpiresDate(objectMetadata.getHttpExpiresDate());
		com.setLastModified(objectMetadata.getLastModified());
		com.setOngoingRestore((objectMetadata.getOngoingRestore()==null)?false:objectMetadata.getOngoingRestore());
		com.setRestoreExpirationTime(objectMetadata.getRestoreExpirationTime());
		com.setSSEAlgorithm(objectMetadata.getSSEAlgorithm());
		com.setSSECustomerAlgorithm(objectMetadata.getSSECustomerAlgorithm());
		com.setSSECustomerKeyMd5(objectMetadata.getSSECustomerKeyMd5());
		return com;
	}

	public ObjectMetadata setS3CustomObjectMetadataForUpload(S3CustomObjectMetadata com, ObjectMetadata objectMetadata){
		objectMetadata.addUserMetadata("keyName", com.getKeyName());
		objectMetadata.addUserMetadata("bucketName", com.getBucketName());
		objectMetadata.addUserMetadata("objectFilePath", com.getObjectFilePath());
		objectMetadata.addUserMetadata("sizeInKB", String.valueOf(com.getSizeInKB()));
		
		return objectMetadata;
	}

	
	public S3CustomObjectMetadata setS3CustomObjectMetadataAfterDownload(S3CustomObjectMetadata com, ObjectMetadata objectMetadata){
		com.setKeyName(objectMetadata.getUserMetaDataOf("keyName"));
		com.setBucketName(objectMetadata.getUserMetaDataOf("bucketName"));
		com.setObjectFilePath(objectMetadata.getUserMetaDataOf("objectFilePath"));
		com.setSizeInKB(Long.parseLong(objectMetadata.getUserMetaDataOf("sizeInKB")));
		return com;
	}

	public void uploadVideoObject(VideoObjectMetadata vom, AmazonS3Client s3Client){
		System.out.println("S3ObjectService - Preparing Upload for "+ vom.getKeyName());
		if(vom.getContentLength()<=0){
			try {
				FileInputStream stream = new FileInputStream(vom.getObjectFilePath());
				vom.setContentLength(Long.valueOf(IOUtils.toByteArray(stream).length));
				stream.close();
			} catch (IOException e) {
				System.err.printf("Failed while reading bytes from %s", e.getMessage());
			} 
		}
		ontologyService.createVideoIndividual(vom);
		System.out.println("S3ObjectService - Creating S3 Object Metadata for "+ vom.getKeyName());
		ObjectMetadata objectMetadata=new ObjectMetadata();
		objectMetadata=setS3ObjectMetadataForUpload(vom, objectMetadata);
		objectMetadata=setS3CustomObjectMetadataForUpload(vom, objectMetadata);

		objectMetadata.addUserMetadata("format", vom.getFormat());
		objectMetadata.addUserMetadata("caption", vom.getCaption());
		objectMetadata.addUserMetadata("language", vom.getLanguage());
		objectMetadata.addUserMetadata("lengthInSeconds", String.valueOf(vom.getLengthInSeconds()));
		s3ObjectDao.uploadObject(objectMetadata,vom,s3Client);
	}

	public List<VideoObjectMetadata> searchVideoByLanguage(String language, AmazonS3Client s3Client){

		List<VideoObjectMetadata> vomList= new ArrayList<VideoObjectMetadata>();
		List<Map<String,String>> mapList=ontologyService.searchVideoByLanguage(language); 
		//mapList has keyname-bucketname pairs
		List<ObjectMetadata> list=s3ObjectDao.getObjectListByKeyNames(mapList,s3Client);
		while(!list.isEmpty()){
			VideoObjectMetadata vom=new VideoObjectMetadata();
			ObjectMetadata objectMetadata=list.remove(0);
			vom=(VideoObjectMetadata)setS3ObjectMetadataAfterDownload(vom, objectMetadata);
			vom=(VideoObjectMetadata)setS3CustomObjectMetadataAfterDownload(vom, objectMetadata);
			vom.setFormat(objectMetadata.getUserMetaDataOf("format"));
			vom.setFormat(objectMetadata.getUserMetaDataOf("caption"));
			vom.setFormat(objectMetadata.getUserMetaDataOf("language"));
			vom.setFormat(objectMetadata.getUserMetaDataOf("lengthInSeconds"));
			vom.setUrl(objectMetadata.getUserMetaDataOf("url"));
			vomList.add(vom);
		}
		return vomList;


	}
}
