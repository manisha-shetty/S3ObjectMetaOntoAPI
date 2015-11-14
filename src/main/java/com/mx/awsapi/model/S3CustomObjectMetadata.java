package com.mx.awsapi.model;

import com.amazonaws.services.s3.model.ObjectMetadata;

public class S3CustomObjectMetadata extends ObjectMetadata{

	private String keyName;
	private String bucketName;
	private String objectFilePath;
	private long sizeInKB;
	private String url;

	public String getKeyName() {
		return keyName;
	}
	public void setKeyName(String keyName) {
		this.keyName = keyName;
	}
	
	public String getBucketName() {
		return bucketName;
	}
	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}
	public String getObjectFilePath() {
		return objectFilePath;
	}
	public void setObjectFilePath(String objectFilePath) {
		this.objectFilePath = objectFilePath;
	}

	public long getSizeInKB() {
		return sizeInKB;
	}
	
	public void setSizeInKB(long sizeInKB) {
		this.sizeInKB = sizeInKB;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
}
