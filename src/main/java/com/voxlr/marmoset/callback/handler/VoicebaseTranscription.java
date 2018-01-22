package com.voxlr.marmoset.callback.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.voxlr.marmoset.callback.Callback;
import com.voxlr.marmoset.callback.CallbackBody;
import com.voxlr.marmoset.callback.CallbackHandler;
import com.voxlr.marmoset.model.dto.CallbackResult;
import com.voxlr.marmoset.service.CallbackService.CallbackType;
import com.voxlr.marmoset.service.CallbackService.Platform;
import com.voxlr.marmoset.service.TranscriptionService;
import com.voxlr.marmoset.util.exception.CallbackException;

@Callback(
	type = CallbackType.TRANSCRIPTION,
	methods = { RequestMethod.POST },
	platform = Platform.VOICEBASE)
public class VoicebaseTranscription extends CallbackHandler<String> {
    
    @Autowired
    private TranscriptionService transcriptionService;

    @Override
    public CallbackResult<String> handleRequest(String requestPath, CallbackBody callbackBody)
	    throws CallbackException {
	try {
	    ObjectNode response = callbackBody.getJsonBody();
	    transcriptionService.processTranscription(response, Platform.TWILIO);
	} catch (Exception e) {
	    throw new CallbackException("Unable to handle transcription result", e);
	}
	return new CallbackResult<String>("success", MediaType.TEXT_PLAIN);
    }

}
