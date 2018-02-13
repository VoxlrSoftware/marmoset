package com.voxlr.marmoset;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.voxlr.marmoset.config.properties.AppProperties;
import com.voxlr.marmoset.util.MapperUtils;

@SpringBootApplication
@Configuration
@EnableMongoRepositories(basePackages = "com.voxlr.marmoset.repositories")
public class MarmosetApplication {
    
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
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
    public ObjectMapper objectMapper() {
	return new ObjectMapper();
    }
    
    public static void main(String[] args) {
	SpringApplication.run(MarmosetApplication.class, args);
    }
}
