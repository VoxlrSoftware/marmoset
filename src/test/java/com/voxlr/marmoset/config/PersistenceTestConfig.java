package com.voxlr.marmoset.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.voxlr.marmoset.util.PersistenceUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.voxlr")
public class PersistenceTestConfig {

  @Bean
  public PersistenceUtils persistenceUtils() {
    return new PersistenceUtils();
  }

  @Bean
  public ObjectMapper objectMapper() {
    return new ObjectMapper();
  }
}
