package com.voxlr.marmoset.model.persistence;

import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "teams")
@EnableMongoAuditing
@Getter
@Setter
@NoArgsConstructor
public class Team extends AuditModel {
    
    @NotNull
    private String name;
}
