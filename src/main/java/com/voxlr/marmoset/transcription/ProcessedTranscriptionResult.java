package com.voxlr.marmoset.transcription;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProcessedTranscriptionResult {
    private String result;
    private String entityId;
    private int employeeTalkTime;
    private int customerTalkTime;
    private Exception error;
    
    public ProcessedTranscriptionResult(String entityId) {
	this.entityId = entityId;
    }
    
    public ProcessedTranscriptionResult(String entityId, Exception error) {
	this.entityId = entityId;
	this.error = error;
    }
    
    public boolean hasError() {
	return error != null;
    }
}
