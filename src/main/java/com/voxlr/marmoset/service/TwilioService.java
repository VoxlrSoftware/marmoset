package com.voxlr.marmoset.service;

import static com.google.common.collect.Lists.newArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import com.twilio.jwt.client.ClientCapability;
import com.twilio.jwt.client.OutgoingClientScope;
import com.voxlr.marmoset.config.properties.TwilioProperties;

@Service
@EnableConfigurationProperties(TwilioProperties.class)
public class TwilioService {

    @Autowired
    private TwilioProperties twilioProperties;
    
    public String getClientToken() {
	OutgoingClientScope outgoingClientScope = 
		new OutgoingClientScope.Builder(twilioProperties.getTwiml()).build();
	ClientCapability capability = 
		new ClientCapability.Builder(twilioProperties.getSid(), twilioProperties.getToken())
		.scopes(newArrayList(outgoingClientScope)).build();
	
	return capability.toJwt();
    }
}
