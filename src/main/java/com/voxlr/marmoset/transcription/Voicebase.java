package com.voxlr.marmoset.transcription;

import static com.voxlr.marmoset.util.JsonUtils.safeGetAsString;
import static com.voxlr.marmoset.util.PathUtils.combinePaths;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import com.amazonaws.util.Base64;
import com.fasterxml.jackson.databind.JsonNode;
import com.voxlr.marmoset.config.properties.AppProperties;
import com.voxlr.marmoset.controller.CallbackController;
import com.voxlr.marmoset.service.CallbackService;
import com.voxlr.marmoset.service.CallbackService.CallbackType;
import com.voxlr.marmoset.service.CallbackService.Platform;

public class Voicebase {

    public static final String VOICEBASE_URL_PATH = "https://apis.voicebase.com/v3/media";
    public final String VOICEBASE_CONFIG;
    
    public Voicebase(AppProperties appProperties) {
	this.VOICEBASE_CONFIG = buildConfig(appProperties);
    }
    
    private String buildConfig(AppProperties appProperties) {
	String callbackUrl = combinePaths(
		appProperties.getExternalApiUrl(),
		CallbackController.CALLBACK,
		CallbackService.generatePath(CallbackType.TRANSCRIPTION, Platform.VOICEBASE));
		
	JsonObjectBuilder config = Json.createObjectBuilder();
	
	JsonObject knowledge = Json.createObjectBuilder()
		.add("enableDiscovery", true)
		.add("enableExternalDataSources", true)
		.build();
	
	config.add("knowledge", knowledge);
	
	JsonObject ingest = Json.createObjectBuilder()
		.add("stereo", Json.createObjectBuilder()
			.add("left", Json.createObjectBuilder()
				.add("speakerName", "Employee")
				.build())
			.add("right", Json.createObjectBuilder()
				.add("speakerName", "Customer")
				.build()))
		.build();
	
	config.add("ingest", ingest);
	
	JsonObject publish = Json.createObjectBuilder()
		.add("callbacks", Json.createArrayBuilder()
			.add(Json.createObjectBuilder()
				.add("url", callbackUrl)
				.add("method", "POST")
				.add("include", Json.createArrayBuilder()
					.add("metadata")
					.add("knowledge")
					.add("transcript")
					.build())
				.build())
			.build())
		.build();
	
	config.add("publish", publish);
	
	return config.build().toString();
    }
    
    public ProcessedTranscriptionResult processTranscription(JsonNode request) {
	String entityId = safeGetAsString(request, "mediaId");
	String processedTranscription = null;
	
	try {
	    String transcript = safeGetAsString(request, "transcript.alternateFormats[3].data");
	    if (transcript != null) {
		processedTranscription = new String(Base64.decode(transcript), "utf-8");
	    }
	} catch (Exception e) {
	    return new ProcessedTranscriptionResult(entityId, e);
	}
	
	
	return new ProcessedTranscriptionResult(entityId, processedTranscription);
    }
}