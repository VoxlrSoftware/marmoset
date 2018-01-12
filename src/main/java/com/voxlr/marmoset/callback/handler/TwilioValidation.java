package com.voxlr.marmoset.callback.handler;

import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.voxlr.marmoset.callback.Callback;
import com.voxlr.marmoset.callback.CallbackHandler;
import com.voxlr.marmoset.model.dto.CallbackResult;
import com.voxlr.marmoset.service.CallbackService.CallbackType;
import com.voxlr.marmoset.service.CallbackService.Platform;
import com.voxlr.marmoset.service.ValidationRequestService;

@Callback(
	type = CallbackType.VALIDATION,
	methods = { RequestMethod.POST },
	platform = Platform.TWILIO)
public class TwilioValidation implements CallbackHandler {
    
    private ValidationRequestService validationRequestService;

    @Override
    public CallbackResult handleRequest(String requestPath, ObjectNode body) {
	String callSid = body.get("CallSid").get(0).asText();
	boolean isValid = body.get("VerificationStatus").get(0).asText().equals("success");
	
	validationRequestService.handleValidationResponse(callSid, isValid);
	
	return new CallbackResult();
    }

    @Override
    public void initialize(ApplicationContext applicationContext) {
	validationRequestService = applicationContext.getBean(ValidationRequestService.class);
    }

}
