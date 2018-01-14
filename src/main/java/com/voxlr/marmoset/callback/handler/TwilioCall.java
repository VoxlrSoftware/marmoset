package com.voxlr.marmoset.callback.handler;

import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.voxlr.marmoset.callback.Callback;
import com.voxlr.marmoset.callback.CallbackHandler;
import com.voxlr.marmoset.model.dto.CallbackResult;
import com.voxlr.marmoset.service.CallbackService.CallbackType;
import com.voxlr.marmoset.service.CallbackService.Platform;

@Callback(
	type = CallbackType.CALL,
	methods = { RequestMethod.POST },
	platform = Platform.TWILIO)
public class TwilioCall implements CallbackHandler {

    @Override
    public CallbackResult handleRequest(String requestPath, ObjectNode body) {
	
	return new CallbackResult();
    }

    @Override
    public void initialize(ApplicationContext applicationContext) {
	// TODO Auto-generated method stub
	
    }

}
