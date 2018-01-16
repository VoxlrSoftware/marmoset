package com.voxlr.marmoset.controller;

import java.io.IOException;
import java.util.Map;

import javax.naming.spi.DirStateFactory.Result;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerMapping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.voxlr.marmoset.callback.CallbackBody;
import com.voxlr.marmoset.model.dto.CallbackResult;
import com.voxlr.marmoset.service.CallbackService;
import com.voxlr.marmoset.util.exception.CallbackException;
import com.voxlr.marmoset.util.exception.HandlerNotFoundException;

@RestController
public class CallbackController extends ApiController {
    public static final String CALLBACK = "/callback";
    public static final String PARAM_TYPE = "/{type}";
    public static final String PARAM_PLATFORM = "/{platform}";
    
    @Autowired
    private CallbackService callbackService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @RequestMapping(
	    value = CALLBACK + PARAM_TYPE + PARAM_PLATFORM,
	    method = { RequestMethod.POST, RequestMethod.PUT },
	    consumes = {
		    MediaType.APPLICATION_FORM_URLENCODED_VALUE,
		    MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8",
		    MediaType.APPLICATION_JSON_VALUE,
		    MediaType.APPLICATION_JSON_UTF8_VALUE
		    })
    public ResponseEntity<?> postCallback(
	    @PathVariable String type,
	    @PathVariable String platform,
	    HttpServletRequest request) throws HandlerNotFoundException, CallbackException {
	String requestPath = (String)request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);

	ObjectNode body = objectMapper.valueToTree(request.getParameterMap());
	
	CallbackResult<?> result = callbackService.getHandler(type, platform, request.getMethod())
		.handleRequest(requestPath, new CallbackBody(body));
	
	HttpHeaders headers = new HttpHeaders();
        headers.setContentType(result.getContentType());
        
	return new ResponseEntity<>(result, headers, result.getStatus());
    }
    
    @RequestMapping(
	    value = CALLBACK + PARAM_TYPE + PARAM_PLATFORM,
	    method = RequestMethod.GET)
    public ResponseEntity<?> getCallback(
	    @PathVariable String type,
	    @PathVariable String platform,
	    HttpServletRequest request) throws HandlerNotFoundException, CallbackException {
	String requestPath = (String)request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
	
	CallbackResult result = callbackService.getHandler(type, platform, request.getMethod())
		.handleRequest(requestPath, null);
	return new ResponseEntity<CallbackResult>(result, result.getStatus());
    }
}
