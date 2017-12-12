package com.voxlr.marmoset.model.persistence;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "companies")
@EnableMongoAuditing
@Getter
@Setter
@NoArgsConstructor
@CompoundIndexes({
    @CompoundIndex(name = "callStrategyId", def = "{'callStrategies.id' : 1 }")
})
public class Company extends AuditModel {
    
    @NotBlank
    private String name;
    
    private List<CallStrategy> callStrategies = new ArrayList<>();

    public Company(String name) {
	this.name = name;
    }
}
