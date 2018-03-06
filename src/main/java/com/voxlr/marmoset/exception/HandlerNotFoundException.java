package com.voxlr.marmoset.exception;

@SuppressWarnings("serial")
public class HandlerNotFoundException extends Exception {
    
    public HandlerNotFoundException(String type, String platform, String method) {
	super("Handler not supported for /" + type + " (" + platform + ") using method [" + method + "]");
    }

}
