package com.voxlr.marmoset.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.convert.CustomConversions;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;

@Configuration
@EnableMongoAuditing
@EnableMongoRepositories(basePackages = "com.voxlr.marmoset.repositories")
@EnableConfigurationProperties(MongoProperties.class)
public class MongoConfig extends AbstractMongoConfiguration {
    
    @Autowired
    private MongoProperties mongoProperties;
    
    private final String basePackage = "com.voxlr.marmoset";
    
    private final List<Converter<?, ?>> converters = new ArrayList<Converter<?, ?>>();
    
    @Override
    protected String getDatabaseName() {
        return mongoProperties.getDatabase();
    }

    @Override
    public Mongo mongo() throws Exception {
	String host = mongoProperties.getHost();
	String port = mongoProperties.getPort();
        return new MongoClient(host, Integer.parseInt(port)); 
    }

    @Override
    public String getMappingBasePackage() {
        return basePackage;
    }
    
    @Override
    public CustomConversions customConversions() {
        return new CustomConversions(converters);
    }
}
