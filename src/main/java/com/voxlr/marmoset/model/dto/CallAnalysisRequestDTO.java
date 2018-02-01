package com.voxlr.marmoset.model.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
