package com.mx.awsapi.model;

import java.util.Date;

import com.amazonaws.services.s3.model.ObjectMetadata;

public class S3CustomObjectMetadata extends ObjectMetadata{

	private String bucketName; // keyname+bucketname=identifier : An unambiguous reference to the resource within a given context.
	private Date created; //Date of creation of the resource.
	private String creator; //An entity primarily responsible for making the resource.
	private String description; //An account of the resource.
	private double extent; //The size or duration of the resource.
	private String format; //The file format, physical medium, or dimensions of the resource.
	private String keyName; // keyname+bucketname=identifier : An unambiguous reference to the resource within a given context.
	private String objectFilePath;
	private String title;	//A name given to the resource.
	private String url;
	
	public String getBucketName() {
		return bucketName;
	}
	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public double getExtent() {
		return extent;
	}
	public void setExtent(double extent) {
		this.extent = extent;
	}
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
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
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	
}
