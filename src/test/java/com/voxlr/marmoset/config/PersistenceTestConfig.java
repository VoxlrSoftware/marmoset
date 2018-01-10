package com.voxlr.marmoset.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.voxlr.marmoset.util.PersistenceUtils;

@Configuration
@ComponentScan(basePackages = "com.voxlr")
public class PersistenceTestConfig {
    
    @Bean
    public PersistenceUtils persistenceUtils() {
	return new PersistenceUtils();
    }

}