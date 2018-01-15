package com.voxlr.marmoset.callback.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMethod;

import com.voxlr.marmoset.callback.Callback;
import com.voxlr.marmoset.callback.CallbackBody;
import com.voxlr.marmoset.callback.CallbackHandler;
import com.voxlr.marmoset.model.dto.CallbackResult;
import com.voxlr.marmoset.service.CallService;
import com.voxlr.marmoset.service.CallbackService.CallbackType;
import com.voxlr.marmoset.service.CallbackService.Platform;
import com.voxlr.marmoset.service.TwilioService;

@Callback(
	type = CallbackType.CALL,
	methods = { RequestMethod.POST },
	platform = Platform.TWILIO)
public class TwilioCall extends CallbackHandler {

    @Autowired
    private TwilioService twilioService;
    
    @Autowired
    private CallService callService;
    
    public CallbackResult handleRequest(String requestPath, CallbackBody callbackBody) {
	String callerId = callbackBody.getString("callerId");
	String customuerNumber = callbackBody.getString("customerNumber");
	String companyId = callbackBody.getString("companyId");
	String userId = callbackBody.getString("userId");
	String selectedTemplateName = callbackBody.getString("selectedTemplateName");
	
	
	if (twilioService.initializeCall(callerId)) {
	    
	}
	return new CallbackResult();
    }

}
