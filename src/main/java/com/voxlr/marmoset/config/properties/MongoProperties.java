package com.voxlr.marmoset.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("mongo")
@Getter
@Setter
public class MongoProperties {

  private String host;
  private String port;
  private String database;

  public String getMongoUrl() {
    return "mongodb://" + host + ":" + port + "/" + database;
  }
}
