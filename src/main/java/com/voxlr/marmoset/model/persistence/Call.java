package com.voxlr.marmoset.model.persistence;

import static com.voxlr.marmoset.model.CallOutcome.NONE;

import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
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
@EnableMongoAuditing
@Getter
@Setter
@NoArgsConstructor
@CompoundIndexes({
    @CompoundIndex(name = "companyId_createDate_outcome", def = "{'companyId' : 1, 'createDate': 1, 'outcome': 1}"),
    @CompoundIndex(name = "userId_createDate_outcome", def = "{'userId': 1, 'createDate': 1, 'outcome': 1}")
})
@Builder
@AllArgsConstructor
public class Call extends AuditModel implements UserScopedEntity {
    @NotNull
    private String companyId;
    @NotNull
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

    @Field("outcome")
    private String callOutcome = NONE;
    
    private CallStrategy callStrategy;
    
    @Field("stats")
    private CallStatistic statistics = new CallStatistic();
    @DBRef(lazy = true)
    private CallAnalysis analysis;
    
    public static enum DBField {
	RECORDING_URL("recordingUrl"),
	TRANSCRIPTION_ID("transcriptionId"),
	CALL_OUTCOME("callOutcome"),
	CALL_STRATEGY("callStrategy"),
	STATISTICS("statistics"),
	ANALYSIS("analysis")
	;
	
	private String fieldName;
	
	private DBField(String fieldName) {
	    this.fieldName = fieldName;
	}
	
	public String get() {
	    return fieldName;
	}
    }
}
