package com.voxlr.marmoset.jms.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import com.voxlr.marmoset.jms.SQSConfig;
import com.voxlr.marmoset.jms.model.CallRecordingRequest;
import com.voxlr.marmoset.jms.model.CallRecordingResult;
import com.voxlr.marmoset.model.persistence.Call;
import com.voxlr.marmoset.service.CallService;
import com.voxlr.marmoset.service.RecordingService;
import com.voxlr.marmoset.service.TranscriptionService;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class CallRecordingConsumer extends JMSConsumer {
    
    @Autowired
    private RecordingService recordingService;
    
    @Autowired
    private CallService callService;
    
    @Autowired
    private TranscriptionService transcriptionService;

    @JmsListener(destination = SQSConfig.QUEUE_CALL_RECORDING)
    public void handleCallRecordingRequest(String requestJson) throws Exception {
	CallRecordingRequest request = parseRequest(requestJson, CallRecordingRequest.class);
	
	if (request != null) {
	    log.debug("Received request for call recording");
	    recordingService.handleRecording(request);
	}
    }
    
    @JmsListener(destination = SQSConfig.QUEUE_CALL_RECORDING_RESULT)
    public void handleCallRecordingResult(String requestJson) throws Exception {
	CallRecordingResult result = parseRequest(requestJson, CallRecordingResult.class);
	
	if (result != null) {
	    log.debug("Received call recording result");
	    Call call = callService.updateCallRecording(result);
	    String transcriptionId = transcriptionService.transcribeCall(call);
	    if (transcriptionId != null) {
		callService.updateCallTranscription(call, transcriptionId);
	    }
	}
    }

}