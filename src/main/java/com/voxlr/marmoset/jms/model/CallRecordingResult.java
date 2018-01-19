package com.voxlr.marmoset.jms.model;

import com.voxlr.marmoset.model.CallScoped;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CallRecordingResult implements CallScoped {
    private String recordingUrl;
    private String callSid;
    private String callId;
    
    public String getId() {
	return callId;
    }
}
