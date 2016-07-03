package com.mx.awsapi.service;


import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import com.mx.awsapi.dao.S3ObjectDao;
import com.mx.awsapi.model.AudioObjectMetadata;
import com.mx.awsapi.model.DocumentObjectMetadata;
import com.mx.awsapi.model.ImageObjectMetadata;
import com.mx.awsapi.model.S3CustomObjectMetadata;
import com.mx.awsapi.model.VideoObjectMetadata;


public class S3ObjectService {

	//static Logger log = Logger.getLogger(S3ObjectService.class.getName());

	@Autowired
	private S3ObjectDao s3ObjectDao;

	@Autowired
	private OntologyService ontologyService;

	DateFormat df = new SimpleDateFormat("MM/dd/yyyy"); 

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
		objectMetadata.addUserMetadata("bucketName",com.getBucketName());
		objectMetadata.addUserMetadata("created",String.valueOf(com.getCreated()));
		objectMetadata.addUserMetadata("creator",com.getCreator());
		objectMetadata.addUserMetadata("description",com.getDescription());
		objectMetadata.addUserMetadata("extent",String.valueOf(com.getExtent()));
		objectMetadata.addUserMetadata("format",com.getFormat());
		objectMetadata.addUserMetadata("keyName", com.getKeyName());
		objectMetadata.addUserMetadata("objectFilePath", com.getObjectFilePath());
		objectMetadata.addUserMetadata("title",com.getTitle());
		objectMetadata.addUserMetadata("url", com.getUrl());
		return objectMetadata;
	}


	public S3CustomObjectMetadata setS3CustomObjectMetadataAfterDownload(S3CustomObjectMetadata com, ObjectMetadata objectMetadata){
		com.setBucketName(objectMetadata.getUserMetaDataOf("bucketName"));
		try {
			com.setCreated(df.parse(objectMetadata.getUserMetaDataOf("created")));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		com.setCreator(objectMetadata.getUserMetaDataOf("creator"));
		com.setDescription(objectMetadata.getUserMetaDataOf("description"));
		com.setExtent(Double.parseDouble(objectMetadata.getUserMetaDataOf("extent")));
		com.setFormat(objectMetadata.getUserMetaDataOf("format"));
		com.setKeyName(objectMetadata.getUserMetaDataOf("keyName"));
		com.setObjectFilePath(objectMetadata.getUserMetaDataOf("objectFilePath"));
		com.setTitle(objectMetadata.getUserMetaDataOf("title"));
		com.setUrl(objectMetadata.getUserMetaDataOf("url"));
		return com;

	}
	
	/********************UPLOAD OBJECTS***************************/
	
	public void uploadAudioObject(AudioObjectMetadata aom, AmazonS3Client s3Client) throws IOException,MalformedURLException{
		System.out.println("S3ObjectService - Preparing Upload for "+ aom.getKeyName());
		if(aom.getContentLength()<=0){
			try {
				FileInputStream stream = new FileInputStream(aom.getObjectFilePath());
				aom.setContentLength(Long.valueOf(IOUtils.toByteArray(stream).length));
				stream.close();
			} catch (IOException e) {
				System.err.printf("Failed while reading bytes from %s", e.getMessage());
			} 
		}
		ontologyService.createAudioIndividual(aom);
		System.out.println("S3ObjectService - Creating S3 Object Metadata for "+ aom.getKeyName());
		ObjectMetadata objectMetadata=new ObjectMetadata();
		objectMetadata=setS3ObjectMetadataForUpload(aom, objectMetadata);
		objectMetadata=setS3CustomObjectMetadataForUpload(aom, objectMetadata);
		objectMetadata.addUserMetadata("album", df.format(aom.getAlbum()));
		objectMetadata.addUserMetadata("encoder", aom.getEncoder());
		objectMetadata.addUserMetadata("language", aom.getLanguage());
		objectMetadata.addUserMetadata("lengthInSeconds", String.valueOf(aom.getLengthInSeconds()));
		objectMetadata.addUserMetadata("trackNumber", String.valueOf(aom.getTrackNumber()));
		objectMetadata.addUserMetadata("type", aom.getType());
		s3ObjectDao.uploadObject(objectMetadata,aom,s3Client);			
	}

	public void uploadDocumentObject(DocumentObjectMetadata dom, AmazonS3Client s3Client) throws IOException,MalformedURLException{
		System.out.println("S3ObjectService - Preparing Upload for "+ dom.getKeyName());
		if(dom.getContentLength()<=0){
			try {
				FileInputStream stream = new FileInputStream(dom.getObjectFilePath());
				dom.setContentLength(Long.valueOf(IOUtils.toByteArray(stream).length));
				stream.close();
			} catch (IOException e) {
				System.err.printf("Failed while reading bytes from %s", e.getMessage());
			} 
		}
		ontologyService.createDocumentIndividual(dom);
		System.out.println("S3ObjectService - Creating S3 Object Metadata for "+ dom.getKeyName());
		ObjectMetadata objectMetadata=new ObjectMetadata();
		objectMetadata=setS3ObjectMetadataForUpload(dom, objectMetadata);
		objectMetadata=setS3CustomObjectMetadataForUpload(dom, objectMetadata);
		objectMetadata.addUserMetadata("language", dom.getLanguage());
		objectMetadata.addUserMetadata("publisher",dom.getPublisher());
		objectMetadata.addUserMetadata("bibliographicCitation",dom.getBibliographicCitation());
		//s3ObjectDao.uploadObject(objectMetadata,dom,s3Client);			
	}


	public void uploadImageObject(ImageObjectMetadata iom, AmazonS3Client s3Client) throws IOException,MalformedURLException{
		System.out.println("S3ObjectService - Preparing Upload for "+ iom.getKeyName());
		if(iom.getContentLength()<=0){
			try {
				FileInputStream stream = new FileInputStream(iom.getObjectFilePath());
				iom.setContentLength(Long.valueOf(IOUtils.toByteArray(stream).length));
				stream.close();
			} catch (IOException e) {
				System.err.printf("Failed while reading bytes from %s", e.getMessage());
			} 
		}
		ontologyService.createImageIndividual(iom);
		System.out.println("S3ObjectService - Creating S3 Object Metadata for "+ iom.getKeyName());
		ObjectMetadata objectMetadata=new ObjectMetadata();
		objectMetadata=setS3ObjectMetadataForUpload(iom, objectMetadata);
		objectMetadata=setS3CustomObjectMetadataForUpload(iom, objectMetadata);
		objectMetadata.addUserMetadata("colorSpace", iom.getColorSpace());
		objectMetadata.addUserMetadata("imageHeight", String.valueOf(iom.getImageHeight()));
		objectMetadata.addUserMetadata("imageWidth", String.valueOf(iom.getImageWidth()));
		objectMetadata.addUserMetadata("location", iom.getLocation());
		objectMetadata.addUserMetadata("orientation", iom.getOrientation());
		objectMetadata.addUserMetadata("pixels", String.valueOf(iom.getPixels()));
		objectMetadata.addUserMetadata("subject", iom.getSubject());
		s3ObjectDao.uploadObject(objectMetadata,iom,s3Client);			
	}

	public void uploadVideoObject(VideoObjectMetadata vom, AmazonS3Client s3Client) throws IOException,MalformedURLException{
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
		objectMetadata.addUserMetadata("language", vom.getLanguage());
		objectMetadata.addUserMetadata("lengthInSeconds", String.valueOf(vom.getLengthInSeconds()));
		objectMetadata.addUserMetadata("subject", vom.getSubject());
		objectMetadata.addUserMetadata("type",vom.getType());
		s3ObjectDao.uploadObject(objectMetadata,vom,s3Client);			
	}
	
	/********************************SEARCH AUDIO OBJECTS***************************/

	public List<AudioObjectMetadata> searchAudioByCreator(String creator, AmazonS3Client s3Client){
		List<AudioObjectMetadata> aomList= new ArrayList<AudioObjectMetadata>();
		return aomList;
	}
	public List<AudioObjectMetadata> searchAudioByDescription(String description, AmazonS3Client s3Client){
		List<AudioObjectMetadata> aomList= new ArrayList<AudioObjectMetadata>();
		return aomList;
	}
	public List<AudioObjectMetadata> searchAudioByFormat(String format, AmazonS3Client s3Client){
		List<AudioObjectMetadata> aomList= new ArrayList<AudioObjectMetadata>();
		return aomList;
	}
	public List<AudioObjectMetadata> searchAudioByTitle(String title, AmazonS3Client s3Client){
		List<AudioObjectMetadata> aomList= new ArrayList<AudioObjectMetadata>();
		return aomList;
	}
	public List<AudioObjectMetadata> searchAudioByAlbum(String album, AmazonS3Client s3Client){
		List<AudioObjectMetadata> aomList= new ArrayList<AudioObjectMetadata>();
		return aomList;
	}
	public List<AudioObjectMetadata> searchAudioByEncoder(String encoder, AmazonS3Client s3Client){
		List<AudioObjectMetadata> aomList= new ArrayList<AudioObjectMetadata>();
		return aomList;
	}
	public List<AudioObjectMetadata> searchAudioByLanguage(String language, AmazonS3Client s3Client){
		List<AudioObjectMetadata> aomList= new ArrayList<AudioObjectMetadata>();
		return aomList;
	}
	public List<AudioObjectMetadata> searchAudioType(String type, AmazonS3Client s3Client){
		List<AudioObjectMetadata> aomList= new ArrayList<AudioObjectMetadata>();
		return aomList;
	}
	
	/********************************SEARCH DOCUMENT OBJECTS***************************/
	
	public List<DocumentObjectMetadata> searchDocumentByCreator(String creator, AmazonS3Client s3Client){
		List<DocumentObjectMetadata> domList= new ArrayList<DocumentObjectMetadata>();
		return domList;
	}
	public List<DocumentObjectMetadata> searchDocumentByDescription(String description, AmazonS3Client s3Client){
		List<DocumentObjectMetadata> domList= new ArrayList<DocumentObjectMetadata>();
		return domList;
	}
	public List<DocumentObjectMetadata> searchDocumentByFormat(String format, AmazonS3Client s3Client){
		List<DocumentObjectMetadata> domList= new ArrayList<DocumentObjectMetadata>();
		return domList;	
	}
	public List<DocumentObjectMetadata> searchDocumentByTitle(String title, AmazonS3Client s3Client){
		List<DocumentObjectMetadata> domList= new ArrayList<DocumentObjectMetadata>();
		return domList;
	}
	public List<DocumentObjectMetadata> searchDocumentByLanguage(String language, AmazonS3Client s3Client){
		List<DocumentObjectMetadata> domList= new ArrayList<DocumentObjectMetadata>();
		return domList;
	}
	public List<DocumentObjectMetadata> searchDocumentByPublisher(String publisher, AmazonS3Client s3Client){
		List<DocumentObjectMetadata> domList= new ArrayList<DocumentObjectMetadata>();
		return domList;
	}
	public List<DocumentObjectMetadata> searchDocumentByBibliographicCitation(String bibliographicCitation, AmazonS3Client s3Client){
		List<DocumentObjectMetadata> domList= new ArrayList<DocumentObjectMetadata>();
		return domList;
	}
	
	/********************************SEARCH IMAGE OBJECTS***************************/

	public List<ImageObjectMetadata> searchImageByCreator(String creator, AmazonS3Client s3Client){
		List<ImageObjectMetadata> iomList= new ArrayList<ImageObjectMetadata>();
		return iomList;
	}
	public List<ImageObjectMetadata> searchImageByDescription(String description, AmazonS3Client s3Client){
		List<ImageObjectMetadata> iomList= new ArrayList<ImageObjectMetadata>();
		return iomList;
	}
	public List<ImageObjectMetadata> searchImageByFormat(String format, AmazonS3Client s3Client){
		List<ImageObjectMetadata> iomList= new ArrayList<ImageObjectMetadata>();
		return iomList;	
	}
	public List<ImageObjectMetadata> searchImageByTitle(String title, AmazonS3Client s3Client){
		List<ImageObjectMetadata> iomList= new ArrayList<ImageObjectMetadata>();
		return iomList;
	}
	public List<ImageObjectMetadata> searchImageByColorSpace(String colorSpace, AmazonS3Client s3Client){
		List<ImageObjectMetadata> iomList= new ArrayList<ImageObjectMetadata>();
		return iomList;
	}
	public List<ImageObjectMetadata> searchImageByLocation(String location, AmazonS3Client s3Client){
		List<ImageObjectMetadata> iomList= new ArrayList<ImageObjectMetadata>();
		return iomList;
	}
	public List<ImageObjectMetadata> searchImageBySubject(String subject, AmazonS3Client s3Client){
		List<ImageObjectMetadata> iomList= new ArrayList<ImageObjectMetadata>();
		return iomList;
	}

	/********************************SEARCH VIDEO OBJECTS***************************/
	
	public List<VideoObjectMetadata> searchVideoByCreator(String creator, AmazonS3Client s3Client){
		List<VideoObjectMetadata> vomList= new ArrayList<VideoObjectMetadata>();
		return vomList;
	}
	public List<VideoObjectMetadata> searchVideoByDescription(String description, AmazonS3Client s3Client){
		List<VideoObjectMetadata> vomList= new ArrayList<VideoObjectMetadata>();
		return vomList;
	}
	public List<VideoObjectMetadata> searchVideoByFormat(String format, AmazonS3Client s3Client){
		List<VideoObjectMetadata> vomList= new ArrayList<VideoObjectMetadata>();
		return vomList;	
	}
	public List<VideoObjectMetadata> searchVideoByTitle(String title, AmazonS3Client s3Client){
		List<VideoObjectMetadata> vomList= new ArrayList<VideoObjectMetadata>();
		return vomList;
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
			vom.setLanguage(objectMetadata.getUserMetaDataOf("language"));
			vom.setLengthInSeconds(Long.parseLong(objectMetadata.getUserMetaDataOf("lengthInSeconds")));
			vom.setSubject(objectMetadata.getUserMetaDataOf("subject"));
			vom.setType(objectMetadata.getUserMetaDataOf("type"));
			vomList.add(vom);
		}
		//System.out.println(mapList.toString());
		return vomList;
	}

	public List<VideoObjectMetadata> searchVideoBySubject(String subject, AmazonS3Client s3Client){
		List<VideoObjectMetadata> vomList= new ArrayList<VideoObjectMetadata>();
		return vomList;
	}

	public List<VideoObjectMetadata> searchVideoByType(String type, AmazonS3Client s3Client){
		List<VideoObjectMetadata> vomList= new ArrayList<VideoObjectMetadata>();
		return vomList;
	}
	
}
