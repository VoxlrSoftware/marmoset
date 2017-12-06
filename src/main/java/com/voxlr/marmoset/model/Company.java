package com.voxlr.marmoset.model;

import java.util.List;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "companies")
@EnableMongoAuditing
@Getter
@Setter
@NoArgsConstructor
public class Company extends AuditModel {
    
    @NotBlank
    private String name;
    
    List<Team> teams;

    public Company(String name) {
	this.name = name;
    }
}
