package com.voxlr.marmoset;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.voxlr.marmoset.config.properties.AppProperties;
import com.voxlr.marmoset.util.MapperUtils;

@SpringBootApplication
@Configuration
public class MarmosetApplication {
    
    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
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
        return jacksonObjectMapperBuilder -> 
            jacksonObjectMapperBuilder.timeZone(TimeZone.getDefault());
    }
    
//    @Bean
//    public ObjectMapper objectMapper() {
//	ObjectMapper objectMapper = new ObjectMapper();
//	objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//	return new ObjectMapper();
//    }
    
    public static void main(String[] args) {
	SpringApplication.run(MarmosetApplication.class, args);
    }
}
