package com.voxlr.marmoset.callback.handler;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.voxlr.marmoset.callback.Callback;
import com.voxlr.marmoset.callback.CallbackBody;
import com.voxlr.marmoset.callback.CallbackHandler;
import com.voxlr.marmoset.model.dto.CallbackResult;
import com.voxlr.marmoset.service.CallbackService.CallbackType;
import com.voxlr.marmoset.service.CallbackService.Platform;
import com.voxlr.marmoset.util.exception.CallbackException;

@Callback(
	type = CallbackType.TRANSCRIPTION,
	methods = { RequestMethod.POST },
	platform = Platform.VOICEBASE)
public class VoicebaseTranscription extends CallbackHandler<String> {

    @Override
    public CallbackResult<String> handleRequest(String requestPath, CallbackBody callbackBody)
	    throws CallbackException {
	try {
	    ObjectNode response = callbackBody.getJsonBody();
	    String mediaId = callbackBody.getBodyString("mediaId");
	    // save off transcription and post queue to process
	    int i = 1;
	} catch (Exception e) {}
	// TODO Auto-generated method stub
	
	return new CallbackResult<String>("success", MediaType.TEXT_PLAIN);
    }

}
