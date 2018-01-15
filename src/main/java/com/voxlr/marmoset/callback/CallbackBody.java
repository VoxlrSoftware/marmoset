package com.voxlr.marmoset.callback;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.Getter;

public class CallbackBody {
    @Getter
    private ObjectNode body;
    
    public CallbackBody(ObjectNode body) {
	this.body = body;
    }
    
    public String getString(String key) {
	return getValue(key).asText();
    }
    
    public JsonNode getValue(String key) {
	JsonNode node = body.get(key);
	
	if (node.isArray()) {
	    node = node.get(0);
	}
	
	return node;
    }
}
