package com.voxlr.marmoset.config;

import java.util.ArrayList;
import java.util.List;

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
public class MongoConfig extends AbstractMongoConfiguration {
    private final List<Converter<?, ?>> converters = new ArrayList<Converter<?, ?>>();
    
    @Override
    protected String getDatabaseName() {
        return "marmoset";
    }

    @Override
    public Mongo mongo() throws Exception {
        return new MongoClient("127.0.0.1", 29017); 
    }

    @Override
    public String getMappingBasePackage() {
        return "com.voxlr.marmoset";
    }
    
    @Override
    public CustomConversions customConversions() {
        return new CustomConversions(converters);
    }
}
