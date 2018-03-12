package com.voxlr.marmoset.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "twilio")
@Getter
@Setter
public class TwilioProperties {
  private String sid;
  private String token;
  private String twiml;
}
