package com.voxlr.marmoset.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class FileStoreService {

    @Autowired
    private AmazonS3 amazonS3;
    
    private final Pattern pattern = Pattern.compile("^.*\\/(voxlr\\-store(\\/[a-zA-Z\\-]+)+)\\/([a-zA-Z0-9\\_]+\\.\\w+)$");
    
    public String readString(String url) throws Exception {
	Matcher matcher = pattern.matcher(url);
	if (!matcher.matches()) {
	    throw new Exception("Invalid AWS url [" + url + "]");
	}
	
	String bucketName = matcher.group(1);
	String key = matcher.group(3);
	GetObjectRequest request = new GetObjectRequest(bucketName, key);
	S3Object object = amazonS3.getObject(request);
	return IOUtils.toString(object.getObjectContent(), StandardCharsets.UTF_8);
    }
    
    public String uploadRemoteFile(String fileUri, String key, String bucketName) throws Exception {
	InputStream inputStream = null;
	
	try {
	    log.debug("Uploading [" + key + "] to s3");
	    URL url = new URL(fileUri);
	    URLConnection urlConnection = url.openConnection();
	    inputStream = urlConnection.getInputStream();
	    ObjectMetadata objectMetadata = new ObjectMetadata();
	    objectMetadata.setContentType(urlConnection.getContentType());
	    objectMetadata.setContentLength(urlConnection.getContentLength());
	    return uploadStream(inputStream, key, bucketName, objectMetadata);
	} catch (Exception e) {
	    log.error("Error retrieving file to upload to s3", e);
	    throw e;
	} finally {
	    if (inputStream != null) {
		inputStream.close();
	    }
	}
    }
    
    public String uploadString(String content, String key, String bucketName) throws Exception {
	return uploadString(content, key, bucketName, new ObjectMetadata());
    }
    
    public String uploadString(String content, String key, String bucketName, ObjectMetadata objectMetadata) throws Exception {
	InputStream inputStream = null;
	try {
	    byte[] contentAsBytes = content.getBytes(StandardCharsets.UTF_8);
	    inputStream = new ByteArrayInputStream(contentAsBytes);
	    objectMetadata.setContentLength(contentAsBytes.length);
	    return uploadStream(inputStream, key, bucketName, objectMetadata);
	} catch (Exception e) {
	    log.error("Error handling string upload to s3", e);
	    throw e;
	} finally {
	    if (inputStream != null) {
		inputStream.close();
	    }
	}
    }
    
    public String uploadStream(InputStream inputStream, String key, String bucketName) throws Exception {
	return uploadStream(inputStream, key, bucketName, new ObjectMetadata());
    }
    
    public String uploadStream(InputStream inputStream, String key, String bucketName, ObjectMetadata objectMetadata) throws Exception {
	try {
	    amazonS3.putObject(bucketName, key, inputStream, objectMetadata);
	    URL uploadedUrl = amazonS3.getUrl(bucketName, key);
	    return uploadedUrl.toString();
	} catch (Exception e) {
	    log.error("Error uploading string to s3", e);
	    throw e;
	}
    }
}
