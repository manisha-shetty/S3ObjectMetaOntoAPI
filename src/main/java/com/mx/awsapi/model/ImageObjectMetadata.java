package com.mx.awsapi.model;

import java.util.Date;

public class ImageObjectMetadata extends S3CustomObjectMetadata{

	private String colorSpace;
	private long imageHeight;
	private long imageWidth;
	private String location;
	private String orientation;
	private long pixels;
	private String subject;  //The topic of the resource.
	
	public String getColorSpace() {
		return colorSpace;
	}
	public void setColorSpace(String colorSpace) {
		this.colorSpace = colorSpace;
	}
	public long getImageHeight() {
		return imageHeight;
	}
	public void setImageHeight(long imageHeight) {
		this.imageHeight = imageHeight;
	}
	public long getImageWidth() {
		return imageWidth;
	}
	public void setImageWidth(long imageWidth) {
		this.imageWidth = imageWidth;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getOrientation() {
		return orientation;
	}
	public void setOrientation(String orientation) {
		this.orientation = orientation;
	}
	public long getPixels() {
		return pixels;
	}
	public void setPixels(long pixels) {
		this.pixels = pixels;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}

	
}
