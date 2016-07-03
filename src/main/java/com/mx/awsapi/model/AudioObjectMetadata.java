package com.mx.awsapi.model;

import java.util.Date;

public class AudioObjectMetadata extends S3CustomObjectMetadata{

	private String album;
	private String encoder;
	private String language; //A language of the resource.
	private long lengthInSeconds;
	private int trackNumber;
	private String type;  //The nature or genre of the resource.
	
	public String getAlbum() {
		return album;
	}
	public void setAlbum(String album) {
		this.album = album;
	}
	public String getEncoder() {
		return encoder;
	}
	public void setEncoder(String encoder) {
		this.encoder = encoder;
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
	public int getTrackNumber() {
		return trackNumber;
	}
	public void setTrackNumber(int trackNumber) {
		this.trackNumber = trackNumber;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	
}
	