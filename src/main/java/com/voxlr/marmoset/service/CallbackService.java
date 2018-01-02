package com.voxlr.marmoset.service;

import static com.voxlr.marmoset.util.AnnotationUtils.getAnnotatedClasses;
import static com.voxlr.marmoset.util.AnnotationUtils.getAnnotationMembers;

import java.util.HashMap;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.voxlr.marmoset.callback.Callback;
import com.voxlr.marmoset.callback.CallbackHandler;
import com.voxlr.marmoset.model.dto.CallbackResult;
import com.voxlr.marmoset.util.exception.HandlerNotFoundException;

@Service
public class CallbackService {
    public static final String PATH_RECORDING = "/recording";
    
    private final HashMap<String, CallbackHandler> callbackHandlers = 
	    new HashMap<>();
    
    public CallbackService() {
	Set<Class<?>> handlers = 
		getAnnotatedClasses("com.voxlr.marmoset.callback.handler", Callback.class);
	
	handlers.stream().forEach(handlerClass -> {
	    try {
		if (CallbackHandler.class.isAssignableFrom(handlerClass)) {
		    CallbackHandler handler = (CallbackHandler) handlerClass.newInstance();
		    HashMap<String, Object> methodMap = getAnnotationMembers(
			    handlerClass,
			    Callback.class);
		    
		    String path = (String) methodMap.get("forPath");
		    if (path == null) throw new Exception("@Callback must have an associated path for class [" +
			    handlerClass.getName() + "].");
		    if (callbackHandlers.containsKey(path)) throw new Exception("Duplicate callback path for class [" + 
			    handlerClass.getName() + "].");
		    
		    callbackHandlers.put(path, handler);
		}
	    } catch (Exception e) {
		throw new RuntimeException("Illegal use of @Callback", e);
	    }
	});
    }
    
    public CallbackResult handleCallback(String path, ObjectNode body) throws HandlerNotFoundException {
	if (!callbackHandlers.containsKey(path)) {
	    throw new HandlerNotFoundException(path);
	}
	
	return callbackHandlers.get(path).handleRequest(body);
    }
}
