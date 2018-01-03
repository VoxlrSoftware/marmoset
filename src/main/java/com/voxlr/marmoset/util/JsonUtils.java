package com.voxlr.marmoset.util;

import static com.voxlr.marmoset.util.StreamUtils.asStream;

import java.io.StringReader;
import java.util.List;
import java.util.stream.Collectors;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;

public class JsonUtils {
    
    public static JsonObject jsonFromString(String jsonObjectStr) {
	JsonReader jsonReader = Json.createReader(new StringReader(jsonObjectStr));
	JsonObject object = jsonReader.readObject();
	jsonReader.close();
	return object;
    }
    
    public static List<JsonValue> pluck(JsonArray array, String fieldName) {
	return asStream(array.iterator()).map(x -> x.asJsonObject().get(fieldName)).collect(Collectors.toList());
    }
}
