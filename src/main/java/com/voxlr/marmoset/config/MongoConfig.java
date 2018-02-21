package com.voxlr.marmoset.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import com.voxlr.marmoset.converter.DateTimeConverter;
import com.voxlr.marmoset.model.persistence.lifecycle.EntityLifeCycle;

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
