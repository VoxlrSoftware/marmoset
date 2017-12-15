package com.voxlr.marmoset.util.json;

import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

public class JsonUtils {
    
    public static JsonObject jsonFromString(String jsonObjectStr) {
	JsonReader jsonReader = Json.createReader(new StringReader(jsonObjectStr));
	JsonObject object = jsonReader.readObject();
	jsonReader.close();
	return object;
    }
}
