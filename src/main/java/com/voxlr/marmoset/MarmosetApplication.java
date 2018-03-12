package com.voxlr.marmoset;

import com.voxlr.marmoset.config.properties.AppProperties;
import com.voxlr.marmoset.util.MapperUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.TimeZone;

@SpringBootApplication
@Configuration
public class MarmosetApplication {

  @Bean
  public BCryptPasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
    return new PropertySourcesPlaceholderConfigurer();
  }

  @Bean
  public static MapperUtils mapperUtils() {
    return new MapperUtils();
  }

  @Bean
  public AppProperties appProperties() {
    return new AppProperties();
  }

  @Bean
  public Jackson2ObjectMapperBuilderCustomizer jacksonObjectMapperCustomization() {
    return jacksonObjectMapperBuilder -> jacksonObjectMapperBuilder.timeZone(TimeZone.getDefault());
  }

  public static void main(String[] args) {
    SpringApplication.run(MarmosetApplication.class, args);
  }
}
