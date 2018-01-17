package com.voxlr.marmoset.jms.model;

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
public class CallRecordingRequest {
    private String recordingUrl;
    private String callSid;
    
    @Builder.Default
    private String extension = "";
}
