package com.voxlr.marmoset.model.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CallAnalysisRequestDTO {
  private List<String> searchPhrases;
  private String text;
  private String callId;
  private String callbackUrl;
}
