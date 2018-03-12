package com.voxlr.marmoset.aggregation.operation;

import org.bson.Document;

public abstract class CustomOperation {
  private static final String $ = "$";

  protected String internal(String field) {
    return $ + field;
  }

  public CustomAggregationOperation build() {
    CustomAggregationOperation customOperation = new CustomAggregationOperation();
    doBuild(customOperation.getOperation());
    return customOperation;
  }

  protected abstract void doBuild(Document customOperation);
}
