package com.voxlr.marmoset.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.mongobee.Mongobee;

@Configuration
@EnableConfigurationProperties(MongoProperties.class)
public class MongobeeConfig {
    
    @Autowired
    private MongoProperties mongoProperties;
    
    private final String changeLogPackage = "com.voxlr.marmoset.changelogs";
    
    @Bean
    public Mongobee mongobee() {
	String url = mongoProperties.getMongoUrl();
	
	Mongobee runner = new Mongobee(url);
	runner.setChangeLogsScanPackage(changeLogPackage);
	return runner;
    }
}
