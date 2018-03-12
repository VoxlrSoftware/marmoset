package com.voxlr.marmoset.aggregation.operation;

import lombok.Getter;
import org.bson.Document;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperationContext;

@Getter
public class CustomAggregationOperation implements AggregationOperation {
  private Document operation;

  public CustomAggregationOperation() {
    this.operation = new Document();
  }

  public CustomAggregationOperation(Document operation) {
    this.operation = operation;
  }

  @Override
  public Document toDocument(AggregationOperationContext context) {
    return context.getMappedObject(operation);
  }
}
