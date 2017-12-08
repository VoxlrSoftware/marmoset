package com.voxlr.marmoset.model.persistence;

import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.mapping.Document;

import com.voxlr.marmoset.model.CompanyScopedEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "teams")
@EnableMongoAuditing
@Getter
@Setter
@NoArgsConstructor
public class Team extends AuditModel implements CompanyScopedEntity {
    
    @NotNull
    private String name;
    
    @NotNull
    private String companyId;
}
