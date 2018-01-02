package com.voxlr.marmoset.util.exception;

@SuppressWarnings("serial")
public class HandlerNotFoundException extends Exception {
    
    public HandlerNotFoundException(String handlerPath) {
	super("Handler not found for path [" + handlerPath + "].");
    }

}
