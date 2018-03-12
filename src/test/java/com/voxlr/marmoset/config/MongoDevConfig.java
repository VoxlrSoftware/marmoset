package com.voxlr.marmoset.config;

import com.mongodb.MongoClient;
import cz.jirutka.spring.embedmongo.EmbeddedMongoFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;

import java.io.IOException;

@Profile("test")
@Configuration
@EnableMongoAuditing
public class MongoDevConfig {

  private static final String MONGO_DB_URL = "localhost";
  private static final String MONGO_DB_NAME = "embeded_db";

  @Bean
  public MongoTemplate mongoTemplate() throws IOException {
    EmbeddedMongoFactoryBean mongo = new EmbeddedMongoFactoryBean();
    mongo.setBindIp(MONGO_DB_URL);
    MongoClient mongoClient = mongo.getObject();
    mongoClient.dropDatabase(MONGO_DB_NAME);
    MongoTemplate mongoTemplate = new MongoTemplate(mongoClient, MONGO_DB_NAME);

    MappingMongoConverter mongoMapping = (MappingMongoConverter) mongoTemplate.getConverter();
    mongoMapping.setCustomConversions(MongoConfig.customConversions());
    mongoMapping.afterPropertiesSet();
    return mongoTemplate;
  }
}
