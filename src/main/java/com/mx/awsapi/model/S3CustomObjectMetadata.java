package com.mx.awsapi.model;

import com.amazonaws.services.s3.model.ObjectMetadata;

public class S3CustomObjectMetadata extends ObjectMetadata{

	private String keyName;
	private String objectFilePath;
	//private String id;
	private long sizeInKB;
	
	public String getKeyName() {
		return keyName;
	}
	public void setKeyName(String keyName) {
		this.keyName = keyName;
	}
	
	public String getObjectFilePath() {
		return objectFilePath;
	}
	public void setObjectFilePath(String objectFilePath) {
		this.objectFilePath = objectFilePath;
	}
//	public String getId() {
//		return id;
//	}
//	public void setId(String id) {
//		this.id = id;
//	}
	public long getSizeInKB() {
		return sizeInKB;
	}
	public void setSizeInKB(long sizeInKB) {
		this.sizeInKB = sizeInKB;
	}
	
	
	
}
