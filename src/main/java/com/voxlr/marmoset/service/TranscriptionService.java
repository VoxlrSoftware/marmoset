package com.voxlr.marmoset.service;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.voxlr.marmoset.config.S3Config;
import com.voxlr.marmoset.model.persistence.Call;
import com.voxlr.marmoset.service.CallbackService.Platform;
import com.voxlr.marmoset.service.domain.CallService;
import com.voxlr.marmoset.transcription.ProcessedTranscriptionResult;
import lombok.extern.log4j.Log4j2;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static com.voxlr.marmoset.model.persistence.factory.CallUpdate.anUpdate;

@Service
@Log4j2
public class TranscriptionService {

  @Autowired private ObjectMapper objectMapper;

  @Autowired private FileStoreService fileStoreService;

  @Autowired private CallService callService;

  @Autowired private VoicebaseService voicebase;

  @Autowired private CallAnalysisRequestService callAnalysisService;

  public String transcribeCall(Call call) {
    String recordingPath = call.getRecordingUrl();
    if (recordingPath != null) {
      return sendForTranscription(call.getId(), recordingPath);
    }

    return null;
  }

  private String sendForTranscription(String callId, String recordingPath) {
    try {
      log.debug("Sending recording of call [" + callId + "] for transcription");
      CloseableHttpClient client = HttpClientBuilder.create().build();
      HttpPost post = new HttpPost(VoicebaseService.VOICEBASE_URL_PATH);
      post.addHeader(
          HttpHeaders.AUTHORIZATION,
          "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJjNTU5NWI2OC1iODYwLTQ3NjUtYmFhNS00Zjg4ODNjZjZlMGEiLCJ1c2VySWQiOiJhdXRoMHw1OTMzMTRhMjM1MjRjZTU4ODVkZjUyNjIiLCJvcmdhbml6YXRpb25JZCI6IjkyMWI2ZDA0LTE3ZWEtNjA4YS0zZGUwLWYwY2YyM2Y3NDA2MiIsImVwaGVtZXJhbCI6ZmFsc2UsImlhdCI6MTUwMjYzNDYzMTUxNCwiaXNzIjoiaHR0cDovL3d3dy52b2ljZWJhc2UuY29tIn0.4wKHo-3D5bIsJMHff16QbSmdZrX8gLbK6jXaIV-mTKU");

      MultipartEntityBuilder builder = MultipartEntityBuilder.create();
      builder.addTextBody("mediaUrl", recordingPath);
      builder.addTextBody("configuration", voicebase.buildConfig());
      HttpEntity entity = builder.build();

      post.setEntity(entity);
      CloseableHttpResponse response = client.execute(post);

      if (response.getStatusLine().getStatusCode() != 200) {
        throw new Exception("Unable to send for transcription");
      }

      String responseValue = EntityUtils.toString(response.getEntity());
      JsonNode object = objectMapper.readTree(responseValue);
      return object.get("mediaId").asText();
    } catch (Exception e) {
      log.error("Unable to send call for transcription", e);
      return null;
    }
  }

  @Async("transcribeExecutor")
  public void processTranscription(ObjectNode request, Platform platform) {
    log.debug("processing transcription for platform: " + platform.getName());
    ProcessedTranscriptionResult result = voicebase.processTranscription((JsonNode) request);
    if (!result.hasError()) {
      try {
        Call call = callService.getByTranscriptionId(result.getEntityId());

        ObjectMetadata metadata = new ObjectMetadata();
        Map<String, String> userMetadata = new HashMap<String, String>();
        userMetadata.put("callId", call.getId());
        userMetadata.put("transcriptionId", result.getEntityId());
        userMetadata.put("platform", platform.getName());
        metadata.setUserMetadata(userMetadata);

        String transcriptionUrl =
            fileStoreService.uploadString(
                result.getResult(),
                call.getId() + "_" + result.getEntityId().substring(0, 6) + ".raw",
                S3Config.VOXLR_STORE_TRANSCRIPTS,
                metadata);
        call =
            callService.updateInternal(
                anUpdate(call)
                    .withTranscriptionUrl(transcriptionUrl)
                    .withEmployeeTalkTime(result.getEmployeeTalkTime())
                    .withCustomerTalkTime(result.getCustomerTalkTime())
                    .withTotalTalkTime(result.getTotalTalkTime()));
        callAnalysisService.createAnalysisRequest(call);
      } catch (Exception e) {
        log.error("Unable to process transcription callback", e);
      }
    }
  }
}
