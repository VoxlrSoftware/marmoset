package com.voxlr.marmoset.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.MongoClient;
import com.voxlr.marmoset.config.properties.MongoProperties;

@Profile("!dev")
@Configuration
@EnableMongoAuditing
@EnableMongoRepositories(basePackages = "com.voxlr.marmoset.repositories")
@EnableConfigurationProperties(MongoProperties.class)
public class MongoProdConfig {
    
    @Autowired
    private MongoProperties mongoProperties;
    
    @Bean
    public MongoDbFactory mongoDbFactory() {
	String host = mongoProperties.getHost();
	String port = mongoProperties.getPort();
	MongoClient client = new MongoClient(host, Integer.parseInt(port));
	return new SimpleMongoDbFactory(client, mongoProperties.getDatabase());
    }
}
