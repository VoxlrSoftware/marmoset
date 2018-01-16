package com.voxlr.marmoset.callback.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;

import com.voxlr.marmoset.callback.Callback;
import com.voxlr.marmoset.callback.CallbackBody;
import com.voxlr.marmoset.callback.CallbackHandler;
import com.voxlr.marmoset.model.dto.CallbackResult;
import com.voxlr.marmoset.model.persistence.Call;
import com.voxlr.marmoset.model.persistence.CallRequest;
import com.voxlr.marmoset.service.CallService;
import com.voxlr.marmoset.service.CallbackService.CallbackType;
import com.voxlr.marmoset.service.CallbackService.Platform;
import com.voxlr.marmoset.util.exception.CallbackException;
import com.voxlr.marmoset.service.TwilioService;

@Callback(
	type = CallbackType.CALL,
	methods = { RequestMethod.POST },
	platform = Platform.TWILIO)
public class TwilioCall extends CallbackHandler<String> {

    @Autowired
    private TwilioService twilioService;
    
    @Autowired
    private CallService callService;
    
    public CallbackResult<String> handleRequest(String requestPath, CallbackBody callbackBody) throws CallbackException {
	String requestId = callbackBody.getString("requestId");
	String callSid = callbackBody.getString("CallSid");
	
	try {
	    CallRequest callRequest = callService.getRequest(requestId);
	    Call call = callService.create(callRequest, callSid);
	    String response = twilioService.initializeCall(call);
	    return new CallbackResult<String>(response, MediaType.APPLICATION_XML);
	    
	} catch (Exception e) {
	    throw new CallbackException("Unable to issue new call for requestId [" + requestId + "]", e);
	}
    }

}
