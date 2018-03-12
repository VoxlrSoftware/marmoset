package com.voxlr.marmoset.util;

import com.fasterxml.jackson.databind.JsonNode;

import javax.json.*;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.voxlr.marmoset.util.StreamUtils.asStream;

public class JsonUtils {

  public static JsonObject jsonFromString(String jsonObjectStr) {
    JsonReader jsonReader = Json.createReader(new StringReader(jsonObjectStr));
    JsonObject object = jsonReader.readObject();
    jsonReader.close();
    return object;
  }

  public static List<JsonValue> pluck(JsonArray array, String fieldName) {
    return asStream(array.iterator())
        .map(x -> x.asJsonObject().get(fieldName))
        .collect(Collectors.toList());
  }

  public static String safeGetAsString(JsonNode node, String path) {
    JsonNode jsonNode;
    return ((jsonNode = safeGet(node, path)) != null) ? jsonNode.asText() : null;
  }

  public static boolean safeGetAsBoolean(JsonNode node, String path) {
    JsonNode jsonNode;
    return ((jsonNode = safeGet(node, path)) != null) ? jsonNode.asBoolean() : false;
  }

  public static JsonNode safeGet(JsonNode node, String path) {
    return safeGet(node, Arrays.asList(path.split("[\\[.\\]]")));
  }

  public static JsonNode safeGet(JsonNode node, List<String> path) {
    if (node == null) {
      return null;
    }

    String pathToProcess = path.get(0);

    if (!isNullOrEmpty(pathToProcess)) {
      node = getNextNode(node, pathToProcess);
    }

    if (path.size() > 1) {
      return safeGet(node, path.subList(1, path.size()));
    }

    return node;
  }

  private static JsonNode getNextNode(JsonNode node, String pathToProcess) {
    if (Pattern.matches("^\\d+$", pathToProcess)) {
      return node.get(Integer.parseInt(pathToProcess));
    }

    return node.get(pathToProcess);
  }
}
