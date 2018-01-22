package com.voxlr.marmoset.service;

import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.voxlr.marmoset.config.S3Config;
import com.voxlr.marmoset.config.properties.AppProperties;
import com.voxlr.marmoset.model.persistence.Call;
import com.voxlr.marmoset.service.CallbackService.Platform;
import com.voxlr.marmoset.transcription.ProcessedTranscriptionResult;
import com.voxlr.marmoset.transcription.Voicebase;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class TranscriptionService implements InitializingBean {
    
    @Autowired
    private AppProperties appProperties;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private FileStoreService fileStoreService;
    
    @Autowired
    private CallService callService;
    
    private Voicebase voicebase;
    
    public String transcribeCall(Call call) {
	String recordingPath = call.getRecordingUrl();
	if (recordingPath != null) {
	    return sendForTranscription(call.getId(), recordingPath);
	}
	
	return null;
    }
    
    private String sendForTranscription(String callId, String recordingPath) {
	try {

	    CloseableHttpClient client = HttpClientBuilder.create().build();
	    HttpPost post = new HttpPost(Voicebase.VOICEBASE_URL_PATH);
	    post.addHeader(HttpHeaders.AUTHORIZATION, "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJjNTU5NWI2OC1iODYwLTQ3NjUtYmFhNS00Zjg4ODNjZjZlMGEiLCJ1c2VySWQiOiJhdXRoMHw1OTMzMTRhMjM1MjRjZTU4ODVkZjUyNjIiLCJvcmdhbml6YXRpb25JZCI6IjkyMWI2ZDA0LTE3ZWEtNjA4YS0zZGUwLWYwY2YyM2Y3NDA2MiIsImVwaGVtZXJhbCI6ZmFsc2UsImlhdCI6MTUwMjYzNDYzMTUxNCwiaXNzIjoiaHR0cDovL3d3dy52b2ljZWJhc2UuY29tIn0.4wKHo-3D5bIsJMHff16QbSmdZrX8gLbK6jXaIV-mTKU");
	    
	    MultipartEntityBuilder builder = MultipartEntityBuilder.create();
	    builder.addTextBody("mediaUrl", recordingPath);
	    builder.addTextBody("configuration", voicebase.VOICEBASE_CONFIG);
	    HttpEntity entity = builder.build();
	    
	    post.setEntity(entity);
	    CloseableHttpResponse response = client.execute(post);
	    
	    if (response.getStatusLine().getStatusCode() != 200) {
		throw new Exception("Unable to send for transcription");
	    }
	    
	    String responseValue = EntityUtils.toString(response.getEntity());
	    JsonNode object = objectMapper.readTree(responseValue);
	    return object.get("mediaId").asText();
	} catch (Exception e) {
	    log.error("Unable to send call for transcription");
	    return null;
	}
    }
    
    @SuppressWarnings("serial")
    @Async("transcribeExecutor")
    public void processTranscription(ObjectNode request, Platform platform) {
	ProcessedTranscriptionResult result = voicebase.processTranscription((JsonNode)request);
	if (!result.hasError()) {
	    try {
		Call call = callService.getByTranscriptionId(result.getEntityId());
		
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setUserMetadata(new HashMap<String, String>() {{
		    put("callId", call.getId());
		    put("transcriptionId", result.getEntityId());
		    put("platform", platform.getName());
		}});
		
		String transcriptionUrl = fileStoreService.uploadString(
			result.getResult(),
			call.getId() + "_" + result.getEntityId().substring(0, 6),
			S3Config.VOXLR_STORE_TRANSCRIPTS,
			metadata);
		// callService.setTranscriptionUrl(call, transcriptionUrl);
	    } catch (Exception e) {
		log.error("Unable to process transcription callback", e);
	    }
	}
    }

    @Override
    public void afterPropertiesSet() throws Exception {
	voicebase = new Voicebase(appProperties);
    }
}
