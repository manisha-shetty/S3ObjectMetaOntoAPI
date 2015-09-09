package com.mx.awsapi.model;

public class VideoObjectMetadata extends S3CustomObjectMetadata{

		private String format;

		public String getFormat() {
			return format;
		}

		public void setFormat(String format) {
			this.format = format;
		}
		
}
