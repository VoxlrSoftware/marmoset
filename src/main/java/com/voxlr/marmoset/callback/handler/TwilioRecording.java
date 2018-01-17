package com.voxlr.marmoset.callback.handler;

import static com.google.common.base.Strings.isNullOrEmpty;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMethod;

import com.voxlr.marmoset.callback.Callback;
import com.voxlr.marmoset.callback.CallbackBody;
import com.voxlr.marmoset.callback.CallbackHandler;
import com.voxlr.marmoset.jms.model.CallRecordingRequest;
import com.voxlr.marmoset.model.dto.CallbackResult;
import com.voxlr.marmoset.model.persistence.Call;
import com.voxlr.marmoset.service.CallService;
import com.voxlr.marmoset.service.CallbackService.CallbackType;
import com.voxlr.marmoset.service.CallbackService.Platform;
import com.voxlr.marmoset.service.RecordingService;

import lombok.extern.log4j.Log4j2;

@Callback(
	type = CallbackType.RECORDING,
	methods = { RequestMethod.POST },
	platform = Platform.TWILIO)
@Log4j2
public class TwilioRecording extends CallbackHandler<String> {
    
    @Autowired
    private RecordingService recordingService;
    
    @Autowired
    private CallService callService;

    @Override
    public CallbackResult<String> handleRequest(String requestPath, CallbackBody callbackBody) {
	String callSid = callbackBody.getString("CallSid");
	String recordingUrl = callbackBody.getString("RecordingUrl");
	
	String extension = FilenameUtils.getExtension(recordingUrl);
	
	if (isNullOrEmpty(extension)) {
	    extension = "wav";
	    recordingUrl += ".wav";
	}
	
	try {
	    Call call = callService.getByCallSid(callSid);
	    CallRecordingRequest callRecordingRequest = CallRecordingRequest.builder()
		    .recordingUrl(recordingUrl)
		    .callSid(callSid)
		    .extension(extension).build();
	    log.debug("Creating callRecordingRequest [" + callSid + "]");
	    recordingService.postRecording(call, callRecordingRequest);
	} catch (Exception e) { }
	
	
	return CallbackResult.createDefaultResult();
    }

}
