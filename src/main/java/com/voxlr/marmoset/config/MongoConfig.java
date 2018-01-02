package com.voxlr.marmoset.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.voxlr.marmoset.model.persistence.lifecycle.CallLifecycle;

@Configuration
public class MongoConfig {

    @Bean
    public CallLifecycle callLifecycle() {
	return new CallLifecycle();
    }
}
