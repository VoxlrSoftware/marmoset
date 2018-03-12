package com.voxlr.marmoset.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
public class JWTProperties {
  private String clientId;
  private String clientSecret;
  private String signingKey;
  private String resourceId;
  private Long expiration;
}
