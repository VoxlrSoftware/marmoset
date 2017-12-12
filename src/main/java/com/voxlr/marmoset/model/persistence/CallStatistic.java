package com.voxlr.marmoset.model.persistence;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CallStatistic {
    private int duration = 0;
    private int totalTalkTime = 0;
    private int customerTalkTime = 0;
    private int employeeTalkTime = 0;
    private int detectedPhraseCount = 0;
}
