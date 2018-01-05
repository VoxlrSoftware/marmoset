package com.voxlr.marmoset.callback.handler;

import static com.voxlr.marmoset.service.CallbackService.PATH_RECORDING;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.voxlr.marmoset.callback.Callback;
import com.voxlr.marmoset.callback.CallbackHandler;
import com.voxlr.marmoset.model.dto.CallbackResult;

@Callback(forPath = PATH_RECORDING + "/twilio")
public class TwilioRecording implements CallbackHandler {

    @Override
    public CallbackResult handleRequest(ObjectNode body) {
	
	return new CallbackResult();
    }

}
