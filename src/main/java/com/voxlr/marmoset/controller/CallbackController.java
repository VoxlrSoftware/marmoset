package com.voxlr.marmoset.controller;

import static com.voxlr.marmoset.service.CallbackService.PATH_RECORDING;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.voxlr.marmoset.model.dto.CallbackResult;
import com.voxlr.marmoset.service.CallbackService;
import com.voxlr.marmoset.util.exception.HandlerNotFoundException;

@RestController
@RequestMapping("/api/callback")
public class CallbackController {
    
    @Autowired
    private CallbackService callbackService;
    
    @RequestMapping(value = PATH_RECORDING + "/{platform}", method = RequestMethod.POST)
    public ResponseEntity<?> recordingCallback(
	    @PathVariable String platform,
	    @RequestBody ObjectNode body) throws HandlerNotFoundException {
	
	CallbackResult result = callbackService.handleCallback(PATH_RECORDING + "/" + platform, body);
	return new ResponseEntity<CallbackResult>(result, result.getStatus());
    }
}
