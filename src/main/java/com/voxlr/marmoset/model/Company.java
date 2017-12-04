package com.voxlr.marmoset.model;

import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "companies")
@EnableMongoAuditing
public class Company extends AuditModel {
    private String name;
    
    public Company() {}

    public Company(String name) {
	this.name = name;
    }
    
    public String getName() {
	return name;
    }
    
    public void setName(String name) {
	this.name = name;
    }
}
