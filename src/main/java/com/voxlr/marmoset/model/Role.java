package com.voxlr.marmoset.model;

import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

@Document(collection = "roles")
@EnableMongoAuditing
@Getter
@Setter
public class Role {
    private String name;
    private String description;
    
    public Role(String name, String description) {
	this.name = name;
	this.description = description;
    }
}
