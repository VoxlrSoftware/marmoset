package com.voxlr.marmoset.model.persistence;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import com.voxlr.marmoset.model.PhoneNumberHolder;
import com.voxlr.marmoset.validation.constraint.PhoneNumberValidConstraint;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Singular;
import lombok.experimental.Accessors;

@Document(collection = "companies")
@EnableMongoAuditing
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@CompoundIndexes({
    @CompoundIndex(name = "callStrategyId", def = "{'callStrategies.id' : 1 }")
})
@Accessors(chain = true)
public class Company extends AuditModel {
    
    @NotBlank
    private String name;
    
    @PhoneNumberValidConstraint
    private PhoneNumberHolder phoneNumber;
    
    @Singular
    private List<CallStrategy> callStrategies = new ArrayList<CallStrategy>();
}
