package com.voxlr.marmoset.jms.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CallRecordingRequest {
  private String recordingUrl;
  private int recordingDuration;
  private String callSid;
  private String callId;

  @Builder.Default private String extension = "";
}
