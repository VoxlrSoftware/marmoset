package com.voxlr.marmoset.model.dto.aggregation;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class CallAggregateDTO {
    private String id;
    private String userId;
    private String companyId;
    private String callOutcome;
    private String callStrategyName;
    private int totalTalkTime;
    private int duration;
    private int detectedPhraseCount;
    private double detectionRatio;
    private double customerTalkRatio;
}
