package com.voxlr.marmoset.callback.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.voxlr.marmoset.analysis.model.CallAnalysisResult;
import com.voxlr.marmoset.analysis.service.CallAnalysisService;
import com.voxlr.marmoset.callback.Callback;
import com.voxlr.marmoset.callback.CallbackBody;
import com.voxlr.marmoset.callback.CallbackHandler;
import com.voxlr.marmoset.model.dto.CallbackResult;
import com.voxlr.marmoset.service.CallbackService.CallbackType;
import com.voxlr.marmoset.service.CallbackService.Platform;
import com.voxlr.marmoset.util.exception.CallbackException;

import lombok.extern.log4j.Log4j2;

@Callback(
	type = CallbackType.ANALYSIS,
	methods = { RequestMethod.POST },
	platform = Platform.VOXLR)
@Log4j2
public class VoxlrAnalysis extends CallbackHandler<String> {
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private CallAnalysisService callAnalysisService;
    
    @Override
    public CallbackResult<String> handleRequest(String requestPath, CallbackBody callbackBody)
	    throws CallbackException {
	
	try {
	    CallAnalysisResult result = objectMapper.convertValue(callbackBody.getJsonBody(), CallAnalysisResult.class);
	    callAnalysisService.processAnalysis(result);
	} catch (Exception e) {
	    throw new CallbackException("Unable to process request from VoxlrAnalysis", e);
	}
	
	return new CallbackResult<>("OK");
    }

}
