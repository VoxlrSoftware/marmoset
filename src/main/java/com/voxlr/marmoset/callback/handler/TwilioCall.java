package com.voxlr.marmoset.callback.handler;

import com.voxlr.marmoset.callback.Callback;
import com.voxlr.marmoset.callback.CallbackBody;
import com.voxlr.marmoset.callback.CallbackHandler;
import com.voxlr.marmoset.exception.CallbackException;
import com.voxlr.marmoset.model.dto.CallbackResult;
import com.voxlr.marmoset.model.persistence.Call;
import com.voxlr.marmoset.service.CallbackService.CallbackType;
import com.voxlr.marmoset.service.CallbackService.Platform;
import com.voxlr.marmoset.service.TwilioService;
import com.voxlr.marmoset.service.domain.CallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;

@Callback(
  type = CallbackType.CALL,
  methods = {RequestMethod.POST},
  platform = Platform.TWILIO
)
public class TwilioCall extends CallbackHandler<String> {

  @Autowired private TwilioService twilioService;

  @Autowired private CallService callService;

  public CallbackResult<String> handleRequest(String requestPath, CallbackBody callbackBody)
      throws CallbackException {
    String requestId = callbackBody.getParamString("requestId");
    String callSid = callbackBody.getParamString("CallSid");

    try {
      Call call = callService.createFromRequest(requestId, callSid);
      String response = twilioService.initializeCall(call);
      return new CallbackResult<String>(response, MediaType.APPLICATION_XML);

    } catch (Exception e) {
      throw new CallbackException("Unable to issue new call for requestId [" + requestId + "]", e);
    }
  }
}
