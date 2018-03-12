package com.voxlr.marmoset.jms.model;

import com.voxlr.marmoset.model.CallScoped;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CallRecordingResult implements CallScoped {
  private String recordingUrl;
  private int recordingDuration;
  private String callSid;
  private String callId;

  public String getId() {
    return callId;
  }
}
