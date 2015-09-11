package com.mx.awsapi.service;

import org.springframework.beans.factory.annotation.Autowired;

import com.amazonaws.services.s3.model.S3Object;
import com.mx.awsapi.dao.S3ObjectDao;
 

public class S3ObjectService {

	@Autowired
	private S3ObjectDao s3ObjectDao;

	public S3ObjectDao getS3ObjectDao() {
		return s3ObjectDao;
	}

	public void setS3ObjectDao(S3ObjectDao s3ObjectDao) {
		this.s3ObjectDao = s3ObjectDao;
	}

	public void uploadVideoObject(){
		S3Object sobj= new S3Object();
		System.out.println("Uploading object");


	}

}
