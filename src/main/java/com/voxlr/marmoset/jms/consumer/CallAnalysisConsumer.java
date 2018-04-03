package com.voxlr.marmoset.jms.consumer;

import com.voxlr.marmoset.jms.SQSConfig;
import com.voxlr.marmoset.jms.model.CallAnalysisRequest;
import com.voxlr.marmoset.service.CallAnalysisRequestService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@Profile("test")
public class CallAnalysisConsumer extends JMSConsumer {

  @Autowired private CallAnalysisRequestService callAnalysisService;

  @JmsListener(destination = SQSConfig.QUEUE_CALL_ANALYSIS)
  public void handleCallAnalysisRequest(String requestJson) throws Exception {
    CallAnalysisRequest request = parseRequest(requestJson, CallAnalysisRequest.class);

    if (request != null) {
      log.debug("Received request for call analysis");
      callAnalysisService.processAnalysisRequest(request);
    }
  }
}
