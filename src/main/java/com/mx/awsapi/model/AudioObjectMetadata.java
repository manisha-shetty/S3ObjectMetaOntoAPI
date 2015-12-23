package com.mx.awsapi.model;

public class AudioObjectMetadata extends S3CustomObjectMetadata{

	private String format;
	private String caption;
	private String language;
	private long lengthInSeconds;
	
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	public String getCaption() {
		return caption;
	}
	public void setCaption(String caption) {
		this.caption = caption;
	}
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

	

}
