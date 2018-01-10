package com.voxlr.marmoset.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix="twilio")
@Getter
@Setter
public class TwilioProperties {
    private String sid;
    private String token;
    private String twiml;

}
