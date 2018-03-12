package com.voxlr.marmoset.aggregation.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CallAggregateDTO {
  private String id;
  private String userId;
  private String companyId;
  private String callOutcome;
  private String callStrategyName;
  private Integer totalTalkTime;
  private Integer duration;
  private Integer detectedPhraseCount;
  private Double detectionRatio;
  private Double customerTalkRatio;
  private Boolean conversation;
}
