package com.voxlr.marmoset.jms.model;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CallAnalysisRequest {
  private String callId;
  private String transcriptionId;
  private String transcriptionUrl;
}
