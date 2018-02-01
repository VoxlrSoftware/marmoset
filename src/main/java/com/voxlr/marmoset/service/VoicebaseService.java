package com.voxlr.marmoset.service;

import static com.voxlr.marmoset.util.JsonUtils.safeGet;
import static com.voxlr.marmoset.util.JsonUtils.safeGetAsString;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.util.Base64;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.voxlr.marmoset.service.CallbackService.CallbackType;
import com.voxlr.marmoset.service.CallbackService.Platform;
import com.voxlr.marmoset.transcription.ProcessedTranscriptionResult;

import lombok.NoArgsConstructor;

@Service
public class VoicebaseService {

    public static final String VOICEBASE_URL_PATH = "https://apis.voicebase.com/v3/media";
    
    @Autowired
    private CallbackService callbackService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    public String buildConfig() {
	String callbackUrl = callbackService.getCallbackPath(CallbackType.TRANSCRIPTION, Platform.VOICEBASE);
		
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
	ProcessedTranscriptionResult result = new ProcessedTranscriptionResult(entityId);
	String processedTranscription = null;
	
	try {
	    String transcript = safeGetAsString(request, "transcript.alternateFormats[3].data");
	    if (transcript != null) {
		processedTranscription = new String(Base64.decode(transcript), "utf-8");
	    }
	    result.setResult(processedTranscription);
	    JsonNode words = safeGet(request, "transcript.words");
	    calculateStats(words, result);
	} catch (Exception e) {
	    return new ProcessedTranscriptionResult(entityId, e);
	}
	
	
	return result;
    }
    
    private void calculateStats(JsonNode words, ProcessedTranscriptionResult result) {
	if (!words.isArray()) {
	    return;
	}
	
	Map<String, Integer> talkTime = new HashMap<>();
	
	int startMs = 0;
	String speaker = null;
	VBWords vbWords = objectMapper.convertValue(words, VBWords.class);
	List<VBWord> turnWords = vbWords.words.stream().filter(x -> x.isTurn()).collect(Collectors.toList());
	turnWords.add(vbWords.words.get(vbWords.words.size() - 1)); // Last word should trigger a turn calc
	
	for (VBWord word : turnWords) {
	    if (speaker != null) {
		int current = talkTime.containsKey(speaker) ? talkTime.get(speaker) : 0;
		talkTime.put(speaker, startMs - current);
	    }
		
	    speaker = word.word;
	    startMs = Integer.parseInt(word.start);
	}
	
	result.setEmployeeTalkTime(talkTime.get("Employee"));
	result.setCustomerTalkTime(talkTime.get("Customer"));
    }
    
    @NoArgsConstructor
    public static class VBWords {
	public List<VBWord> words;
    }
    
    @NoArgsConstructor
    public static class VBWord {
	@JsonProperty("p")
	public String position;
	
	@JsonProperty("c")
	public String confidence;

	@JsonProperty("s")
	public String start;

	@JsonProperty("e")
	public String end;

	@JsonProperty("m")
	public String metadata;

	@JsonProperty("w")
	public String word;
	
	public boolean isTurn() {
	    return metadata.equals("turn");
	}
    }
}
