package com.voxlr.marmoset.config;

import com.amazon.sqs.javamessaging.ProviderConfiguration;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.destination.DynamicDestinationResolver;

import javax.jms.Session;

@Configuration
@EnableJms
public class JmsConfig {

  private final String accessKey = "AKIAIJ2KCYDSZYYAL2JA";
  private final String secretKey = "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";

  SQSConnectionFactory connectionFactory =
      new SQSConnectionFactory(
          new ProviderConfiguration(),
          AmazonSQSClientBuilder.standard()
              .withRegion(Regions.US_EAST_1)
              .withCredentials(new DefaultAWSCredentialsProviderChain())
              .build());

  @Bean
  public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {
    DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
    factory.setConnectionFactory(this.connectionFactory);
    factory.setDestinationResolver(new DynamicDestinationResolver());
    factory.setConcurrency("3-10");
    factory.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
    return factory;
  }

  @Bean
  public JmsTemplate defaultJmsTemplate() {
    return new JmsTemplate(this.connectionFactory);
  }
}
