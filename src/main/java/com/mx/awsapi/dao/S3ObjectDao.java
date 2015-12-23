package com.mx.awsapi.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.model.Stream;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import com.mx.awsapi.model.S3CustomObjectMetadata;

public class S3ObjectDao {


	public void uploadObject(ObjectMetadata objectMetadata,S3CustomObjectMetadata com, AmazonS3Client s3Client){
		FileInputStream stream;
		try{
			System.out.println("S3ObjectDao - Uploading "+ com.getKeyName());

			stream = new FileInputStream(com.getObjectFilePath());
			//System.out.println(objectMetadata.getContentLength());
			PutObjectRequest putObjectRequest = new PutObjectRequest(com.getBucketName()+"/", com.getKeyName(), stream, objectMetadata);
			PutObjectResult result = s3Client.putObject(putObjectRequest);
			stream.close();
			System.out.println("Etag:" + result.getETag() + "-->" + result);			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			//stream.close();
		}



	}

	public List<ObjectMetadata> getObjectListByKeyNames(List<Map<String,String>> mapList,AmazonS3Client s3Client){

		List<ObjectMetadata> list=new ArrayList<ObjectMetadata>();
		while(!mapList.isEmpty()){
			Map<String,String> map=mapList.remove(0);
			ObjectMetadata objectMetadata=s3Client.getObjectMetadata(map.get("bucketName"),map.get("keyName"));
			objectMetadata.addUserMetadata("url",s3Client.getUrl(map.get("bucketName"),map.get("keyName")).toString());
			list.add(objectMetadata);
		}
		return list;
	}

}
