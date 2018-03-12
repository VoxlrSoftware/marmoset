package com.voxlr.marmoset.config;

import com.github.mongobee.Mongobee;
import com.voxlr.marmoset.config.properties.MongoProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
@EnableConfigurationProperties(MongoProperties.class)
public class MongobeeConfig {

  @Autowired private MongoProperties mongoProperties;

  @Autowired private MongoTemplate mongoTemplate;

  private final String changeLogPackage = "com.voxlr.marmoset.changelogs";

  @Bean
  public Mongobee mongobee() {
    String url = mongoProperties.getMongoUrl();

    Mongobee runner = new Mongobee(url);
    runner.setChangeLogsScanPackage(changeLogPackage);
    runner.setMongoTemplate(mongoTemplate);
    return runner;
  }
}
