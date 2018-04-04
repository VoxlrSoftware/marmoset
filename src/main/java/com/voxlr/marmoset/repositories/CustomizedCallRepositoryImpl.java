package com.voxlr.marmoset.repositories;

import com.voxlr.marmoset.aggregation.AbstractAggregation.RollupCadence;
import com.voxlr.marmoset.aggregation.CallOutcomeAggregation;
import com.voxlr.marmoset.aggregation.dto.AggregateResultDTO;
import com.voxlr.marmoset.aggregation.dto.CallAggregateDTO;
import com.voxlr.marmoset.aggregation.dto.RollupResultDTO;
import com.voxlr.marmoset.aggregation.field.CallAggregationFields.CallField;
import com.voxlr.marmoset.exception.InvalidArgumentsException;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;

import static com.voxlr.marmoset.aggregation.CallAggregation.aCallAggregation;

public class CustomizedCallRepositoryImpl implements CustomizedCallRepository {

  @Autowired private MongoTemplate mongoTemplate;

  @Override
  public Page<CallAggregateDTO> getCallsByCompany(
      ObjectId companyId,
      DateTime startDate,
      DateTime endDate,
      List<CallField> fields,
      Pageable pageable) {
    return aCallAggregation(mongoTemplate)
        .getCallsByCompany(companyId, startDate, endDate, fields, pageable);
  }

  @Override
  public Page<CallAggregateDTO> getCallsByUser(
      ObjectId userId,
      DateTime startDate,
      DateTime endDate,
      List<CallField> fields,
      Pageable pageable) {
    return aCallAggregation(mongoTemplate)
        .getCallsByUser(userId, startDate, endDate, fields, pageable);
  }

  @Override
  public RollupResultDTO averageCallFieldByUser(
      ObjectId userId, DateTime startDate, DateTime endDate, List<CallField> fields)
      throws InvalidArgumentsException {
    return aCallAggregation(mongoTemplate)
        .averageCallFieldsByUser(userId, startDate, endDate, fields);
  }

  @Override
  public RollupResultDTO averageCallFieldByCompany(
      ObjectId companyId, DateTime startDate, DateTime endDate, List<CallField> fields)
      throws InvalidArgumentsException {
    return aCallAggregation(mongoTemplate)
        .averageCallFieldsByCompany(companyId, startDate, endDate, fields);
  }

  @Override
  public List<RollupResultDTO> rollupCallFieldByUser(
      ObjectId userId,
      DateTime startDate,
      DateTime endDate,
      RollupCadence cadence,
      List<CallField> fields)
      throws InvalidArgumentsException {
    return aCallAggregation(mongoTemplate)
        .rollupCallFieldByUser(userId, startDate, endDate, cadence, fields);
  }

  @Override
  public List<RollupResultDTO> rollupCallFieldByCompany(
      ObjectId companyId,
      DateTime startDate,
      DateTime endDate,
      RollupCadence cadence,
      List<CallField> fields)
      throws InvalidArgumentsException {
    return aCallAggregation(mongoTemplate)
        .rollupCallFieldByCompany(companyId, startDate, endDate, cadence, fields);
  }

  @Override
  public AggregateResultDTO getCallOutcomesByCompany(
      ObjectId companyId, DateTime startDate, DateTime endDate) {
    return new CallOutcomeAggregation(mongoTemplate)
        .getCallOutcomesByCompany(companyId, startDate, endDate);
  }

  @Override
  public AggregateResultDTO getCallOutcomesByUser(
      ObjectId userId, DateTime startDate, DateTime endDate) {
    return new CallOutcomeAggregation(mongoTemplate)
        .getCallOutcomesByUser(userId, startDate, endDate);
  }
}
