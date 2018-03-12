package com.voxlr.marmoset.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
@Getter
@Setter
public class AppProperties {
  private String externalUrl;
  private String analysisUrl;

  public String getExternalApiUrl() {
    return externalUrl + "/api";
  }
}
