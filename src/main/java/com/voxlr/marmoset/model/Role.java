package com.voxlr.marmoset.model;

import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "roles")
@EnableMongoAuditing
public class Role {
    private String name;
    private String description;
    
    public Role(String name, String description) {
	this.name = name;
	this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
