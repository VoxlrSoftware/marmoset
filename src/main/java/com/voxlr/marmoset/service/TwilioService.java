package com.voxlr.marmoset.service;

import com.twilio.Twilio;
import com.twilio.exception.ApiException;
import com.twilio.http.HttpMethod;
import com.twilio.jwt.client.ClientCapability;
import com.twilio.jwt.client.OutgoingClientScope;
import com.twilio.rest.api.v2010.account.ValidationRequest;
import com.twilio.rest.api.v2010.account.ValidationRequestCreator;
import com.twilio.twiml.VoiceResponse;
import com.twilio.twiml.voice.Dial;
import com.twilio.twiml.voice.Number;
import com.twilio.type.PhoneNumber;
import com.voxlr.marmoset.config.properties.TwilioProperties;
import com.voxlr.marmoset.model.PhoneNumberHolder;
import com.voxlr.marmoset.model.persistence.Call;
import com.voxlr.marmoset.service.CallbackService.CallbackType;
import com.voxlr.marmoset.service.CallbackService.Platform;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import static com.google.common.collect.Lists.newArrayList;

@Service
@EnableConfigurationProperties(TwilioProperties.class)
public class TwilioService implements InitializingBean {

  @Autowired private TwilioProperties twilioProperties;

  @Autowired private CallbackService callbackService;

  public String getClientToken() {
    OutgoingClientScope outgoingClientScope =
        new OutgoingClientScope.Builder(twilioProperties.getTwiml()).build();
    ClientCapability capability =
        new ClientCapability.Builder(twilioProperties.getSid(), twilioProperties.getToken())
            .scopes(newArrayList(outgoingClientScope))
            .build();

    return capability.toJwt();
  }

  public ValidationRequest validatePhoneNumber(PhoneNumberHolder phoneNumberHolder)
      throws ApiException {
    ValidationRequestCreator creator =
        ValidationRequest.creator(new PhoneNumber(phoneNumberHolder.getNumber()))
            .setStatusCallbackMethod(HttpMethod.POST)
            .setStatusCallback(
                callbackService.getCallbackPath(CallbackType.VALIDATION, Platform.TWILIO));

    if (phoneNumberHolder.hasExtension()) {
      creator.setExtension(phoneNumberHolder.getExtension());
    }

    ValidationRequest request = creator.create();
    return request;
  }

  public String initializeCall(Call callRequest) {
    Number number = new Number.Builder(callRequest.getCustomerNumber().getNumber()).build();
    Dial dial =
        new Dial.Builder()
            .record(Dial.Record.RECORD_FROM_ANSWER_DUAL)
            .recordingStatusCallback(
                callbackService.getCallbackPath(CallbackType.RECORDING, Platform.TWILIO))
            .callerId(callRequest.getEmployeeNumber().getNumber())
            .number(number)
            .build();

    VoiceResponse response = new VoiceResponse.Builder().dial(dial).build();
    return response.toXml();
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    Twilio.init(twilioProperties.getSid(), twilioProperties.getToken());
  }
};
