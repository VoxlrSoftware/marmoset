package com.voxlr.marmoset.service;

import static com.voxlr.marmoset.util.PathUtils.combinePaths;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.voxlr.marmoset.config.properties.AppProperties;
import com.voxlr.marmoset.jms.ProducerService;
import com.voxlr.marmoset.jms.SQSConfig;
import com.voxlr.marmoset.jms.model.CallAnalysisRequest;
import com.voxlr.marmoset.model.dto.CallAnalysisRequestDTO;
import com.voxlr.marmoset.model.persistence.Call;
import com.voxlr.marmoset.service.CallbackService.CallbackType;
import com.voxlr.marmoset.service.CallbackService.Platform;
import com.voxlr.marmoset.service.domain.CallService;
import lombok.extern.log4j.Log4j2;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class CallAnalysisRequestService {

  @Autowired private AppProperties appProperties;

  @Autowired private ProducerService producerService;

  @Autowired private FileStoreService fileStoreService;

  @Autowired public CallService callService;

  @Autowired private CallbackService callbackService;

  @Autowired private ObjectMapper objectMapper;

  public void createAnalysisRequest(Call call) {
    try {
      if (call != null) {
        log.debug("Queueing call to be analyzed");
        CallAnalysisRequest request =
            CallAnalysisRequest.builder()
                .callId(call.getId().toHexString())
                .transcriptionId(call.getTranscriptionId())
                .transcriptionUrl(call.getTranscriptionUrl())
                .build();
        producerService.sendMessage(SQSConfig.QUEUE_CALL_ANALYSIS, request, call.getUserId().toHexString());
      }
    } catch (Exception e) {
      // TODO: Handle requests that fail instead of swallowing them
      log.error("Unable to create analysis request", e);
    }
  }

  public void processAnalysisRequest(CallAnalysisRequest request) {
    try {
      Call call = callService.getInternalByString(request.getCallId());
      log.debug("Processing analysis request for call [" + call.getId() + "]");

      if (!call.getTranscriptionId().equals(request.getTranscriptionId())) {
        return;
      }

      //	    String transcript = obtainTranscript(call);
      //	    String processedTranscript = processPunctuation(transcript);
      //	    analyzeCall(call, processedTranscript);
    } catch (Exception e) {
      log.error(
          "Unable to process call analysis request for call [" + request.getCallId() + "]", e);
    }
  }

  private String obtainTranscript(Call call) throws Exception {
    try {
      return fileStoreService.readString(call.getTranscriptionUrl());
    } catch (Exception e) {
      throw new Exception("Unable to read transcription from url", e);
    }
  }

  private String processPunctuation(String transcript) throws Exception {
    try {
      CloseableHttpClient client = HttpClientBuilder.create().build();
      HttpPost post =
          new HttpPost(combinePaths(appProperties.getAnalysisUrl(), "process_punctuation"));
      MultipartEntityBuilder builder = MultipartEntityBuilder.create();
      builder.addTextBody("text", transcript);
      HttpEntity entity = builder.build();

      post.setEntity(entity);
      CloseableHttpResponse response = client.execute(post);

      if (response.getStatusLine().getStatusCode() != 200) {
        throw new Exception(
            "Received bad status from request [" + response.getStatusLine().toString() + "]");
      }

      return EntityUtils.toString(response.getEntity());
    } catch (Exception e) {
      throw new Exception("Unable to process punctuation", e);
    }
  }

  private void analyzeCall(Call call, String transcript) throws Exception {
    try {
      CloseableHttpClient client = HttpClientBuilder.create().build();
      HttpPost post = new HttpPost(combinePaths(appProperties.getAnalysisUrl(), "findmatches"));

      CallAnalysisRequestDTO requestDTO =
          CallAnalysisRequestDTO.builder()
              .callId(call.getId().toHexString())
              .callbackUrl(callbackService.getCallbackPath(CallbackType.ANALYSIS, Platform.VOXLR))
              .searchPhrases(call.getCallStrategyPhrases())
              .text(transcript)
              .build();
      String requestString = objectMapper.writeValueAsString(requestDTO);
      StringEntity entity = new StringEntity(requestString);
      post.setEntity(entity);
      post.setHeader("Accept", "application/json");
      post.setHeader("Content-type", "application/json");

      CloseableHttpResponse response = client.execute(post);

      if (response.getStatusLine().getStatusCode() != 200) {
        throw new Exception(
            "Received bad status from request [" + response.getStatusLine().toString() + "]");
      }
    } catch (Exception e) {
      throw new Exception("Unable to analyze transcript", e);
    }
  }
}
