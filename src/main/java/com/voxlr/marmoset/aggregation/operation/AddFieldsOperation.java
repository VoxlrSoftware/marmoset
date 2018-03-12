package com.voxlr.marmoset.aggregation.operation;

import com.mongodb.BasicDBObject;
import com.voxlr.marmoset.aggregation.field.AggregationField;
import org.bson.Document;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AddFieldsOperation extends CustomOperation {
  private Map<String, BasicDBObject> setList;

  private AddFieldsOperation() {
    setList = new HashMap<>();
  }

  public static AddFieldsOperation addFields() {
    return new AddFieldsOperation();
  }

  public AddFieldsOperation withFields(String objectName, List<String> fields) {
    BasicDBObject set = new BasicDBObject();
    fields.stream().forEach(field -> set.append(field, internal(field)));

    setList.put(objectName, set);
    return this;
  }

  public AddFieldsOperation withAggregationFields(
      String objectName, List<AggregationField> fields) {
    return withFields(
        objectName,
        (List<String>)
            fields.stream().map(AggregationField::getFieldName).collect(Collectors.toList()));
  }

  @Override
  protected void doBuild(Document customOperation) {
    Document addFields = new Document();
    setList
        .keySet()
        .stream()
        .forEach(
            key -> {
              addFields.append(key, setList.get(key));
            });

    customOperation.append("$addFields", addFields);
  }
}
