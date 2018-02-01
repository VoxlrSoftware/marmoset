package com.voxlr.marmoset.jms.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import com.voxlr.marmoset.jms.SQSConfig;
import com.voxlr.marmoset.jms.model.CallAnalysisRequest;
import com.voxlr.marmoset.service.CallAnalysisRequestService;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class CallAnalysisConsumer extends JMSConsumer {
    
    @Autowired
    private CallAnalysisRequestService callAnalysisService;
    
    @JmsListener(destination = SQSConfig.QUEUE_CALL_ANALYSIS)
    public void handleCallAnalysisRequest(String requestJson) throws Exception {
	CallAnalysisRequest request = parseRequest(requestJson, CallAnalysisRequest.class);
	
	if (request != null) {
	    log.debug("Received request for call analysis");
	    callAnalysisService.processAnalysisRequest(request);
	}
    }
}
