package com.voxlr.marmoset.jms.model;

import com.voxlr.marmoset.model.CallScoped;
import lombok.*;
import org.bson.types.ObjectId;

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

  public ObjectId getId() {
    return new ObjectId(callId);
  }
}
