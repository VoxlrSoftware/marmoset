package com.voxlr.marmoset.repositories;

import com.voxlr.marmoset.aggregation.AbstractAggregation.RollupCadence;
import com.voxlr.marmoset.aggregation.dto.AggregateResultDTO;
import com.voxlr.marmoset.aggregation.dto.CallAggregateDTO;
import com.voxlr.marmoset.aggregation.dto.RollupResultDTO;
import com.voxlr.marmoset.aggregation.field.CallAggregationFields.CallField;
import com.voxlr.marmoset.exception.InvalidArgumentsException;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomizedCallRepository {
  Page<CallAggregateDTO> getCallsByCompany(
      ObjectId companyId,
      DateTime startDate,
      DateTime endDate,
      List<CallField> fields,
      Pageable pageable);

  Page<CallAggregateDTO> getCallsByUser(
      ObjectId userId,
      DateTime startDate,
      DateTime endDate,
      List<CallField> fields,
      Pageable pageable);

  RollupResultDTO averageCallFieldByUser(
      ObjectId userId, DateTime startDate, DateTime endDate, List<CallField> fields)
      throws InvalidArgumentsException;

  RollupResultDTO averageCallFieldByCompany(
      ObjectId companyId, DateTime startDate, DateTime endDate, List<CallField> fields)
      throws InvalidArgumentsException;

  List<RollupResultDTO> rollupCallFieldByUser(
      ObjectId userId,
      DateTime startDate,
      DateTime endDate,
      RollupCadence cadence,
      List<CallField> fields)
      throws InvalidArgumentsException;

  List<RollupResultDTO> rollupCallFieldByCompany(
      ObjectId companyId,
      DateTime startDate,
      DateTime endDate,
      RollupCadence cadence,
      List<CallField> fields)
      throws InvalidArgumentsException;

  AggregateResultDTO getCallOutcomesByCompany(
      ObjectId companyId, DateTime startDate, DateTime endDate);

  AggregateResultDTO getCallOutcomesByUser(ObjectId userId, DateTime startDate, DateTime endDate);
}
