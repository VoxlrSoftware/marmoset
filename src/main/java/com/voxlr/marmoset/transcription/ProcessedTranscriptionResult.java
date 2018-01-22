package com.voxlr.marmoset.transcription;

import lombok.Getter;

@Getter
public class ProcessedTranscriptionResult {
    private String result;
    private String entityId;
    private Exception error;
    
    public ProcessedTranscriptionResult(String entityId, String result) {
	this.entityId = entityId;
	this.result = result;
    }
    
    public ProcessedTranscriptionResult(String entityId, Exception error) {
	this.entityId = entityId;
	this.error = error;
    }
    
    public boolean hasError() {
	return error != null;
    }
}
