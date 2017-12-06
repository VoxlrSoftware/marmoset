package com.voxlr.marmoset.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix="jwt")
@Getter
@Setter
public class JWTProperties {
    private String secret;
    private Long expiration;

}
