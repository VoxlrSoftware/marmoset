package com.voxlr.marmoset.config;

import org.modelmapper.config.Configuration;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Component;

import com.github.rozidan.springboot.modelmapper.ConfigurationConfigurer;

@Component
public class ModelMapperConfig extends ConfigurationConfigurer {
    
    @Override
    public void configure(Configuration configuration) {
        configuration.setMatchingStrategy(MatchingStrategies.STRICT);
    }
}