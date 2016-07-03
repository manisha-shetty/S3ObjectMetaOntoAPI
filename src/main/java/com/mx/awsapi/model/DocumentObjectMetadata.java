package com.mx.awsapi.model;

public class DocumentObjectMetadata extends S3CustomObjectMetadata{

	private String language;  //A language of the resource.
	private String publisher; //An entity responsible for making the resource available.
	private String bibliographicCitation; //A bibliographic reference for the resource.
	
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getPublisher() {
		return publisher;
	}
	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}
	public String getBibliographicCitation() {
		return bibliographicCitation;
	}
	public void setBibliographicCitation(String bibliographicCitation) {
		this.bibliographicCitation = bibliographicCitation;
	}
	
	
}
