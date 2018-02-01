package com.voxlr.marmoset.model.persistence;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.mapping.Document;

import com.voxlr.marmoset.model.CallScopedEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "callAnalysis")
@EnableMongoAuditing
@Getter
@Setter
@NoArgsConstructor
public class CallAnalysis extends AuditModel implements CallScopedEntity {
    private String callId;
    private String callStrategyId;
    private String callStrategyName;
    private List<PhraseAnalysis> phraseAnalysis = newArrayList();
    
    public void addPhraseAnalysis(PhraseAnalysis phrase) {
	this.phraseAnalysis.add(phrase);
    }
    
    public CallAnalysis(Call call) {
	this.callId = call.getId();
	this.callStrategyId = call.getCallStrategy().getId();
	this.callStrategyName = call.getCallStrategy().getName();
    }
}
