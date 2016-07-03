package com.mx.awsapi.model;

public class VideoObjectMetadata extends S3CustomObjectMetadata{

	private String language; //A language of the resource.
	private long lengthInSeconds;
	private String subject; //The topic of the resource.
	private String type; //The nature or genre of the resource.
	
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public long getLengthInSeconds() {
		return lengthInSeconds;
	}
	public void setLengthInSeconds(long lengthInSeconds) {
		this.lengthInSeconds = lengthInSeconds;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	
}
