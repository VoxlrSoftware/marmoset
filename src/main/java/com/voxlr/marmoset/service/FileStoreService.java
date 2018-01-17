package com.voxlr.marmoset.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class FileStoreService {

    @Autowired
    private AmazonS3 amazonS3;
    
    public String uploadFileStream(String fileUri, String key, String bucketName) throws Exception {
	InputStream inputStream = null;
	
	try {
	    log.debug("Uploading [" + key + "] to s3");
	    URL url = new URL(fileUri);
	    URLConnection urlConnection = url.openConnection();
	    inputStream = urlConnection.getInputStream();
	    ObjectMetadata objectMetadata = new ObjectMetadata();
	    objectMetadata.setContentType(urlConnection.getContentType());
	    objectMetadata.setContentLength(urlConnection.getContentLength());
	    amazonS3.putObject(bucketName, key, inputStream, objectMetadata);
	    
	    URL uploadedUrl = amazonS3.getUrl(bucketName, key);
	    return uploadedUrl.toString();
	} catch (Exception e) {
	    log.error("Error uploading file to s3", e);
	    throw e;
	} finally {
	    if (inputStream != null) {
		inputStream.close();
	    }
	}
    }
}
