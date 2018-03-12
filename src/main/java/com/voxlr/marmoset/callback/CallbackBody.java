package com.voxlr.marmoset.callback;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;

public class CallbackBody {
  @Getter private ObjectNode parameters;

  @Getter private String body;

  private ObjectNode jsonBody;

  private ObjectMapper objectMapper = new ObjectMapper();

  public CallbackBody(ObjectNode parameters, String body) {
    this.parameters = parameters;
    this.body = body;
  }

  public String getParamString(String key) {
    JsonNode node = getParamValue(key);
    return node != null ? node.asText() : "";
  }

  public JsonNode getParamValue(String key) {
    JsonNode node = parameters.get(key);

    if (node != null && node.isArray()) {
      node = node.get(0);
    }

    return node;
  }

  public ObjectNode getJsonBody() throws Exception {
    if (jsonBody == null && body != null) {
      jsonBody = (ObjectNode) objectMapper.readTree(body);
    }

    return jsonBody;
  }

  public String getBodyString(String key) throws Exception {
    return getJsonBody().get(key).asText();
  }
}
