package com.voxlr.marmoset.jms.consumer;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.log4j.Log4j2;

@Log4j2
public abstract class JMSConsumer {
    
    @Autowired
    private ObjectMapper objectMapper;

    @SuppressWarnings("unchecked")
    protected <T> T parseRequest(String requestJson, Class<T> clazz) {
	try {
	    return objectMapper.readValue(requestJson, clazz);
	} catch (Exception e) {
	    log.error("Unable to parse message to type [" + clazz.getSimpleName() + "]", e);
	}
	
	return null;
    }
}
