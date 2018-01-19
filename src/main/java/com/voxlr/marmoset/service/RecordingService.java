package com.voxlr.marmoset.service;

import static com.google.common.base.Strings.isNullOrEmpty;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.voxlr.marmoset.config.S3Config;
import com.voxlr.marmoset.jms.ProducerService;
import com.voxlr.marmoset.jms.SQSConfig;
import com.voxlr.marmoset.jms.model.CallRecordingRequest;
import com.voxlr.marmoset.jms.model.CallRecordingResult;
import com.voxlr.marmoset.model.persistence.Call;

@Service
public class RecordingService {
    
    private static final String DEFAULT_PRIORITY = "default";

    @Autowired
    private ProducerService producerService;
    
    @Autowired
    private FileStoreService fileStoreService;
    
    public void postRecordingRequest(Call call, CallRecordingRequest callRecordingRequest) throws Exception {
	producerService.sendMessage(SQSConfig.QUEUE_CALL_RECORDING, callRecordingRequest, call.getUserId());
    }
    
    public void postRecordingResult(CallRecordingResult callRecordingResult) throws Exception {
	producerService.sendMessage(
		SQSConfig.QUEUE_CALL_RECORDING_RESULT,
		callRecordingResult,
		DEFAULT_PRIORITY);
    }
    
    public void handleRecording(CallRecordingRequest callRecordingRequest) throws IOException {
	String newRecordingName = callRecordingRequest.getCallSid();
	
	if (!isNullOrEmpty(callRecordingRequest.getExtension())) {
	    newRecordingName += "." + callRecordingRequest.getExtension();
	}
	
	try {
	    String recordingPath = fileStoreService.uploadFileStream(
		    callRecordingRequest.getRecordingUrl(),
		    newRecordingName,
		    S3Config.VOXLR_STORE_RECORDINGS);
	    CallRecordingResult recordingResult = CallRecordingResult.builder()
		    .callSid(callRecordingRequest.getCallSid())
		    .callId(callRecordingRequest.getCallId())
		    .recordingUrl(recordingPath)
		    .build();
	    postRecordingResult(recordingResult);
	} catch (Exception e) {
	    // TODO: Figure out what we want to do with recordings that fail to upload
	}
    }
}
