package com.voxlr.marmoset.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import org.bson.types.ObjectId;

public class ObjectIdSerializer extends JsonSerializer<ObjectId> {

  @Override
  public void serialize(ObjectId value, JsonGenerator jsonGenerator, SerializerProvider provider) throws IOException {
    jsonGenerator.writeString(value.toHexString());
  }
}
