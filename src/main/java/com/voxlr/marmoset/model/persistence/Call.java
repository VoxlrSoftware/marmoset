package com.voxlr.marmoset.model.persistence;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.mongodb.BasicDBObject;
import com.voxlr.marmoset.model.CompanyScopedEntity;
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
    @CompoundIndex(name = "companyId_createDate", def = "{'companyId' : 1, 'createDate': 1}"),
    @CompoundIndex(name = "userId_createDate", def = "{'userId': 1, 'createDate': 1}")
})
@Builder
@AllArgsConstructor
public class Call extends AuditModel implements CompanyScopedEntity, UserScopedEntity {
    @NotNull
    private String companyId;
    @NotNull
    private String userId;
    @Indexed
    private String callSid;
    private String employeeNumber;
    private String customerNumber;
    private String recordingUrl;
    
    @Builder.Default
    private BasicDBObject externalReferences = new BasicDBObject();
    
    @Builder.Default
    private List<String> strategyList = new ArrayList<>();
    
    @Builder.Default
    private CallStatistic statistics = new CallStatistic();
    @DBRef(lazy = true)
    private CallAnalysis analysis;
}
