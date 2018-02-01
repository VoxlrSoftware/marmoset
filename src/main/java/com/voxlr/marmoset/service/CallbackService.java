package com.voxlr.marmoset.service;

import static com.google.common.collect.Lists.newArrayList;
import static com.voxlr.marmoset.util.AnnotationUtils.getAnnotatedClasses;
import static com.voxlr.marmoset.util.AnnotationUtils.getAnnotationMembers;
import static com.voxlr.marmoset.util.PathUtils.combinePaths;

import java.util.HashMap;
import java.util.Set;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMethod;

import com.voxlr.marmoset.callback.Callback;
import com.voxlr.marmoset.callback.CallbackHandler;
import com.voxlr.marmoset.config.properties.AppProperties;
import com.voxlr.marmoset.util.exception.HandlerNotFoundException;

import lombok.Getter;

@Service
public class CallbackService implements ApplicationContextAware, InitializingBean {
    public static enum CallbackType {
	ANALYSIS("analysis"),
	CALL("call"),
	RECORDING("recording"),
	VALIDATION("validation"),
	TRANSCRIPTION("transcription");
	
	@Getter
	private String name;
	
	CallbackType(String name) {
	    this.name = name;
	}
    };
    
    public static enum Platform {
	VOXLR("voxlr"),
	TWILIO("twilio"),
	VOICEBASE("voicebase");
	
	@Getter
	private String name;
	
	Platform(String name) {
	    this.name = name;
	}
    };
    
    @Autowired
    private ApplicationContext applicationContext;
    
    @Autowired AppProperties appProperties;
    
    public String getCallbackPath(CallbackType type, Platform platform) {
	return combinePaths(appProperties.getExternalApiUrl(), "callback", type.getName(), platform.getName());
    }
    
    private final HashMap<String, CallbackHandler> callbackHandlers = 
	    new HashMap<>();
    
    public CallbackService() {}

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
	this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
	Set<Class<?>> handlers = 
		getAnnotatedClasses("com.voxlr.marmoset.callback.handler", Callback.class);
	
	handlers.stream().forEach(handlerClass -> {
	    try {
		if (CallbackHandler.class.isAssignableFrom(handlerClass)) {
		    CallbackHandler handler = (CallbackHandler) handlerClass.newInstance();
		    handler.initialize(applicationContext);
		    HashMap<String, Object> methodMap = getAnnotationMembers(
			    handlerClass,
			    Callback.class);
		    
		    addHandler(handler, methodMap);
		}
	    } catch (Exception e) {
		throw new RuntimeException("Illegal use of @Callback for handler [" + handlerClass.getName() + "]", e);
	    }
	});
    }
    
    private void addHandler(CallbackHandler handler, HashMap<String, Object> methodMap) {
	RequestMethod[] methods = (RequestMethod[]) methodMap.get("methods");
	CallbackType type = (CallbackType) methodMap.get("type");
	Platform platform = (Platform) methodMap.get("platform");
	
	newArrayList(methods).stream().forEach(method -> {
	    String handlerKey = getHashKey(type.getName(), platform.getName(), method.name());
	    callbackHandlers.put(handlerKey, handler);
	});
    }
    
    private String getHashKey(String typeName, String platformName, String methodName) {
	return String.join("|", new String[] { typeName, platformName, methodName });
    }
    
    public CallbackHandler getHandler(
	    String type,
	    String platform,
	    String method) throws HandlerNotFoundException {
	
	String hashKey = getHashKey(type, platform, method);
	
	if (!callbackHandlers.containsKey(hashKey)) {
	    throw new HandlerNotFoundException(type, platform, method);
	}
	
	return callbackHandlers.get(hashKey);
    }
}
