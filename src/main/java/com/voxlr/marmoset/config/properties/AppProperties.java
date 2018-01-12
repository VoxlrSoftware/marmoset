package com.voxlr.marmoset.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix="app")
@Getter
@Setter
public class AppProperties {
    private String externalUrl;
    
    public String getExternalApiUrl() {
	return externalUrl + "/api";
    }
}
