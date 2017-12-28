package com.voxlr.marmoset.model.persistence;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.Singular;
import lombok.experimental.Accessors;

@Document(collection = "companies")
@EnableMongoAuditing
@Getter
@Setter
@Builder
@CompoundIndexes({
    @CompoundIndex(name = "callStrategyId", def = "{'callStrategies.id' : 1 }")
})
@Accessors(chain = true)
public class Company extends AuditModel {
    
    @NotBlank
    private String name;
    
    @Singular
    private List<CallStrategy> callStrategies;
    
    public Company() {
	callStrategies = new ArrayList<CallStrategy>();
    }

    public Company(String name) {
	super();
	this.name = name;
    }
    
    public Company(String name, List<CallStrategy> callStrategies) {
	this.name = name;
	this.callStrategies = callStrategies;
    }
}
