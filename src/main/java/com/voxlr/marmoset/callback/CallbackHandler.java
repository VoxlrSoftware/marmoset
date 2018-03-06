package com.voxlr.marmoset.callback;

import java.lang.reflect.Field;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.voxlr.marmoset.exception.CallbackException;
import com.voxlr.marmoset.model.dto.CallbackResult;

public abstract class CallbackHandler<T> {
    public abstract CallbackResult<T> handleRequest(String requestPath, CallbackBody callbackBody) throws CallbackException;
    
    public final void initialize(ApplicationContext applicationContext) {
	try {
	    for (Field field : this.getClass().getDeclaredFields()) {
		if (field.isAnnotationPresent(Autowired.class)) {
		    boolean fieldAccess = field.isAccessible();
		    field.setAccessible(true);
		    field.set(this, applicationContext.getBean(field.getType()));
		    field.setAccessible(fieldAccess);
		}
	    }
	} catch (Exception e) {
	    throw new RuntimeException("Unable to initialize CallbackHandler [" + this.getClass() + "]. " +
		    "Check that all fields that require wiring are marked with @Autowired.", e);
	}
    }
}
