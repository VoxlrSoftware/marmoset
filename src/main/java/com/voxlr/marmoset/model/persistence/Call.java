package com.voxlr.marmoset.model.persistence;

import static com.google.common.collect.Lists.newArrayList;
import static com.voxlr.marmoset.model.CallOutcome.NONE;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.voxlr.marmoset.model.PhoneNumberHolder;
import com.voxlr.marmoset.model.UserScopedEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "calls")
@Getter
@Setter
@NoArgsConstructor
@CompoundIndexes({
    @CompoundIndex(name = "companyId_createDate_hasBeenAnalyzed", def = "{'companyId' : 1, 'createDate': 1, 'hasBeenAnalyzed': 1}"),
    @CompoundIndex(name = "teamId_createDate_hasBeenAnalyzed", def = "{'teamId' : 1, 'createDate': 1, 'hasBeenAnalyzed': 1}"),
    @CompoundIndex(name = "userId_createDate_hasBeenAnalyzed", def = "{'userId': 1, 'createDate': 1, 'hasBeenAnalyzed': 1}")
})
@Builder
@AllArgsConstructor
public class Call extends AuditModel implements UserScopedEntity {
    private String companyId;
    private String teamId;
    private String userId;
    @Indexed
    private String callSid;
    @Indexed
    private String transcriptionId;
    
    @Field("empNum")
    private PhoneNumberHolder employeeNumber;
    @Field("custNum")
    private PhoneNumberHolder customerNumber;
    
    @Field("recUrl")
    private String recordingUrl;

    @Field("transUrl")
    private String transcriptionUrl;

    @Field("outcome")
    private String callOutcome = NONE;
    
    @Field("analyzed")
    private boolean hasBeenAnalyzed = false;
    
    @NotNull
    private CallStrategy callStrategy;
    
    @Field("stats")
    private Statistic statistics = new Statistic();
    
    private Analysis analysis = new Analysis();
    
    public List<String> getCallStrategyPhrases() {
	return callStrategy.getPhrases();
    }
    
    @Getter
    @Setter
    public static class Statistic {
        private int duration = 0;
        private int totalTalkTime = 0;
        private int customerTalkTime = 0;
        private int employeeTalkTime = 0;
        
        public Statistic() {
            this.reset();
        }
        
        public void reset() {
            this.duration = 0;
            this.totalTalkTime = 0;
            this.customerTalkTime = 0;
            this.employeeTalkTime = 0;
        }
    }
    
    @Getter
    @Setter
    public static class Analysis {
	    private List<PhraseAnalysis> phraseAnalysis;
	    private double detectionRatio;
	    
	    public Analysis() {
		reset();
	    }
	    
	    public void addPhraseAnalysis(PhraseAnalysis phrase) {
		this.phraseAnalysis.add(phrase);
	    }
	    
	    public Analysis reset() {
		this.phraseAnalysis = newArrayList();
		this.detectionRatio = 0.0;
		return this;
	    }
	}
}
