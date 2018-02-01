package com.voxlr.marmoset.model.persistence;

import static com.google.common.collect.Lists.newArrayList;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PhraseAnalysis {
    public static enum DetectionType {
	MATCH("match"),
	SIMILARITY("similarity");
	
	private String type;
	
	private DetectionType(String type) {
	    this.type = type;
	}
	
	private static final Map<String, DetectionType> detectionMap;
	
	static {
	    detectionMap = new HashMap<>();
	    Arrays.stream(DetectionType.values()).forEach(detection -> {
		detectionMap.put(detection.getType(), detection);
	    });
	}
	
	@JsonValue
	public String getType() {
	    return this.type;
	}
	
	public static DetectionType fromType(String type) {
	    return detectionMap.get(type.toLowerCase());
	}
    };
    
    private String phrase;
    private List<Detection> detections = newArrayList();
    
    public void addDetection(Detection detection) {
	this.detections.add(detection);
    }
    
    public PhraseAnalysis(String phrase) {
	this.phrase = phrase;
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Detection {
	private DetectionType type;
	private String matchedText;
	private double score;
    }
}
