package com.voxlr.marmoset.model;

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
    private String name;

    public Company(String name) {
	this.name = name;
    }
}
