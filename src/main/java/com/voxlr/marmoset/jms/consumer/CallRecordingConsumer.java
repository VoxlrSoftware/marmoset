package com.voxlr.marmoset.jms.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import com.voxlr.marmoset.jms.SQSConfig;
import com.voxlr.marmoset.jms.model.CallRecordingRequest;
import com.voxlr.marmoset.service.RecordingService;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class CallRecordingConsumer extends JMSConsumer<CallRecordingRequest> {
    
    @Autowired
    private RecordingService recordingService;

    @JmsListener(destination = SQSConfig.QUEUE_CALL_RECORDING)
    public void handleRecordingCallback(String requestJson) throws Exception {
	CallRecordingRequest request = parseRequest(requestJson);
	
	if (request != null) {
	    log.debug("Received request for call recording");
	    recordingService.handleRecording(request);
	}
    }

}
