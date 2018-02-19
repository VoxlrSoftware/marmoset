package com.voxlr.marmoset.model.dto;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CallbackResult<T> {
    @JsonIgnore
    private HttpStatus status = HttpStatus.OK;
    @JsonIgnore
    private MediaType contentType = MediaType.APPLICATION_JSON;
    private T result;
    
    public CallbackResult(T result) {
	this.result = result;
    }
    
    public CallbackResult(T result, HttpStatus status) {
	this.result = result;
	this.status = status;
    }
    
    public CallbackResult(T result, MediaType contentType) {
	this.result = result;
	this.contentType = contentType;
    }
    
    public static CallbackResult<String> createDefaultResult() {
	return new CallbackResult<String>("Callback accepted.");
    }

    @JsonIgnore
    public Object getRenderedResult() {
	if (contentType == MediaType.APPLICATION_JSON) {
	    return this;
	}
	
	return result;
    }
}
