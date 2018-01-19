package com.voxlr.marmoset.callback.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMethod;

import com.voxlr.marmoset.callback.Callback;
import com.voxlr.marmoset.callback.CallbackBody;
import com.voxlr.marmoset.callback.CallbackHandler;
import com.voxlr.marmoset.model.dto.CallbackResult;
import com.voxlr.marmoset.service.CallbackService.CallbackType;
import com.voxlr.marmoset.service.CallbackService.Platform;
import com.voxlr.marmoset.service.ValidationRequestService;

@Callback(
	type = CallbackType.VALIDATION,
	methods = { RequestMethod.POST },
	platform = Platform.TWILIO)
public class TwilioValidation extends CallbackHandler<String> {
    
    @Autowired
    private ValidationRequestService validationRequestService;

    public CallbackResult<String> handleRequest(String requestPath, CallbackBody callbackBody) {
	String callSid = callbackBody.getParamValue("CallSid").asText();
	boolean isValid = callbackBody.getParamValue("VerificationStatus").asText().equals("success");
	
	validationRequestService.handleValidationResponse(callSid, isValid);
	
	return CallbackResult.createDefaultResult();
    }

}
