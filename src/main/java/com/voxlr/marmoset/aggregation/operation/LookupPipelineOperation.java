package com.voxlr.marmoset.aggregation.operation;

import static com.google.common.collect.Lists.newArrayList;
import static com.voxlr.marmoset.util.ListUtils.listOf;

import com.mongodb.BasicDBObject;
import java.util.List;
import java.util.stream.Collectors;
import org.bson.Document;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;

public class LookupPipelineOperation extends CustomOperation {
  private String sourceCollection;
  private String resultFieldName;
  private BasicDBObject letVars = new BasicDBObject();
  private List<AggregationOperation> pipeline = newArrayList();

  public static LookupPipelineOperation lookupPipeline(String sourceCollection, String resultFieldName) {
    return new LookupPipelineOperation(sourceCollection, resultFieldName);
  }

  public LookupPipelineOperation(String sourceCollection, String resultFieldName) {
    this.sourceCollection = sourceCollection;
    this.resultFieldName = resultFieldName;
  }

  public LookupPipelineOperation let(String varName, String field) {
    letVars.append(varName, "$$" + field);
    return this;
  }

  public LookupPipelineOperation pipeline(AggregationOperation... aggregationOperations) {
    pipeline.addAll(listOf(aggregationOperations));
    return  this;
  }


  @Override
  protected void doBuild(Document customOperation) {
    Document lookupOperation = new Document();
    lookupOperation.append("from", sourceCollection);
    lookupOperation.append("let", letVars);

    List<Document> pipelineDocuments = pipeline.stream().map(x -> x.toDocument(Aggregation.DEFAULT_CONTEXT)).collect(Collectors.toList());

//    Aggregation pipelineAggregation = newAggregation(pipeline);
//    lookupOperation.append("pipeline", pipelineAggregation.toPipeline(Aggregation.DEFAULT_CONTEXT));
    lookupOperation.append("pipeline", pipelineDocuments);
    lookupOperation.append("as", resultFieldName);

    customOperation.append("$lookup", lookupOperation);
  }
}
