package com.voxlr.marmoset.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties("mongo")
@Getter
@Setter
public class MongoProperties {
    
    private String host;
    private String port;
    private String database;
    
    public String getMongoUrl() {
	return "mongodb://" + host + ":" + port + "/" + database;
    }

}
