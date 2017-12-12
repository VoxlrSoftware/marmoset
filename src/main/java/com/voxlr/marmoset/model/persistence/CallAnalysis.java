package com.voxlr.marmoset.model.persistence;

import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.mapping.Document;

import com.mongodb.BasicDBList;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "callAnalysis")
@EnableMongoAuditing
@Getter
@Setter
@NoArgsConstructor
public class CallAnalysis extends AuditModel {
    private BasicDBList detectedPhrases = new BasicDBList();
}
