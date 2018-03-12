package com.voxlr.marmoset.jms;

import com.amazon.sqs.javamessaging.SQSMessagingClientConstants;
import com.amazon.sqs.javamessaging.message.SQSMessage;
import com.amazon.sqs.javamessaging.message.SQSTextMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
public class ProducerService {

  @Autowired protected JmsTemplate defaultJmsTemplate;

  @Autowired private ObjectMapper objectMapper;

  public void sendMessage(String queueName, Object object) throws Exception {
    sendMessage(queueName, object, null);
  }

  public void sendMessage(String queueName, Object object, String messageGroupId) throws Exception {
    try {
      String request = objectMapper.writeValueAsString(object);
      SQSMessage message = new SQSTextMessage(request);
      message.setStringProperty(SQSMessagingClientConstants.JMSX_GROUP_ID, messageGroupId);
      defaultJmsTemplate.convertAndSend(queueName, message);
    } catch (Exception e) {
      throw new Exception("Unable to send message to queue", e);
    }
  }
}
