package com.voxlr.marmoset.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.voxlr.marmoset.model.persistence.lifecycle.EntityLifeCycle;

@Configuration
public class MongoConfig {

    @Bean
    public EntityLifeCycle entityLifeCycle() {
	return new EntityLifeCycle();
    }
}
