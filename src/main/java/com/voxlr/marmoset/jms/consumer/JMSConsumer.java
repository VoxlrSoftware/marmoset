package com.voxlr.marmoset.jms.consumer;

import java.lang.reflect.ParameterizedType;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.log4j.Log4j2;

@Log4j2
public abstract class JMSConsumer<T> {
    
    @Autowired
    private ObjectMapper objectMapper;

    @SuppressWarnings("unchecked")
    protected T parseRequest(String requestJson) {
	Class<T> typeOfT = (Class<T>)
		((ParameterizedType)getClass()
			.getGenericSuperclass())
		.getActualTypeArguments()[0];
	try {
		return objectMapper.readValue(requestJson, typeOfT);
	} catch (Exception e) {
	    log.error("Unable to parse message to type [" + typeOfT + "]", e);
	}
	
	return null;
    }
}
