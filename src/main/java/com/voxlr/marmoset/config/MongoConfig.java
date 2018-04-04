package com.voxlr.marmoset.config;

import com.voxlr.marmoset.convert.DateTimeConverter;
import com.voxlr.marmoset.convert.ObjectIdConverter;
import com.voxlr.marmoset.model.persistence.lifecycle.EntityLifeCycle;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.util.Arrays;

@Configuration
public class MongoConfig {

  @Bean
  public EntityLifeCycle entityLifeCycle() {
    return new EntityLifeCycle();
  }

  public static MongoCustomConversions customConversions() {
    return new MongoCustomConversions(Arrays.asList(new DateTimeConverter()));
  }
}
