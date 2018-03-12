package com.voxlr.marmoset.aggregation;

import com.voxlr.marmoset.aggregation.dto.CallAggregateDTO;
import com.voxlr.marmoset.aggregation.dto.RollupResultDTO;
import com.voxlr.marmoset.aggregation.field.AggregationField;
import com.voxlr.marmoset.aggregation.field.CallAggregationFields;
import com.voxlr.marmoset.aggregation.field.CallAggregationFields.CallField;
import com.voxlr.marmoset.exception.InvalidArgumentsException;
import com.voxlr.marmoset.model.persistence.Call;
import org.joda.time.DateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.voxlr.marmoset.aggregation.field.AggregationField.*;
import static com.voxlr.marmoset.aggregation.operation.AddFieldsOperation.addFields;
import static com.voxlr.marmoset.aggregation.operation.ProjectFieldsOperation.projectFields;
import static com.voxlr.marmoset.util.ListUtils.listOf;
import static com.voxlr.marmoset.util.ListUtils.reduce;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

public class CallAggregation extends AbstractAggregation<Call> {
  private final List<AggregationField> REQUIRED_FIELDS =
      listOf(
          CallAggregationFields.field(CallField.USER_ID),
          CallAggregationFields.field(CallField.COMPANY_ID),
          CallAggregationFields.field(CallField.CREATE_DATE));

  private final Criteria hasBeenAnalyzed = Criteria.where("hasBeenAnalyzed").is(true);

  public CallAggregation(MongoTemplate mongoTemplate) {
    super(mongoTemplate, Call.class);
  }

  public static CallAggregation aCallAggregation(MongoTemplate mongoTemplate) {
    return new CallAggregation(mongoTemplate);
  }

  public static Criteria getUserConstrained(String userId) {
    return Criteria.where(CallField.USER_ID.get()).is(userId);
  }

  public static Criteria getCompanyConstrained(String companyId) {
    return Criteria.where(CallField.COMPANY_ID.get()).is(companyId);
  }

  public List<AggregationField> getFields(List<CallField> fields) {
    return getFields(fields, true);
  }

  public List<AggregationField> getFields(List<CallField> fields, boolean includeRequired) {
    Set<AggregationField> fieldSet = new HashSet<>();
    if (includeRequired) {
      fieldSet.addAll(REQUIRED_FIELDS);
    }
    fieldSet.addAll(fields.stream().map(CallAggregationFields::field).collect(Collectors.toList()));
    return new ArrayList<AggregationField>(fieldSet);
  }

  private Page<CallAggregateDTO> getCalls(
      Criteria matchCriteria, List<CallField> fields, Pageable pageable) {
    MatchOperation matchOperation = match(matchCriteria);
    ProjectionOperation projectionOperation =
        reduce(project(), getFields(fields), AggregationField::projectField);

    TypedAggregation<Call> aggregation =
        anAggregation()
            .append(matchOperation)
            .withPaging(pageable, sort(Direction.DESC, "createDate"))
            .append(projectionOperation)
            .build();

    return executePagedAggregation(matchOperation, pageable, aggregation, CallAggregateDTO.class);
  }

  public Page<CallAggregateDTO> getCallsByCompany(
      String companyId,
      DateTime startDate,
      DateTime endDate,
      List<CallField> fields,
      Pageable pageable) {
    Criteria matchCriteria =
        getCompanyConstrained(companyId)
            .andOperator(getDateConstrained(startDate, endDate), hasBeenAnalyzed);

    return getCalls(matchCriteria, fields, pageable);
  }

  public Page<CallAggregateDTO> getCallsByUser(
      String userId,
      DateTime startDate,
      DateTime endDate,
      List<CallField> fields,
      Pageable pageable) {
    Criteria matchCriteria =
        getUserConstrained(userId)
            .andOperator(getDateConstrained(startDate, endDate), hasBeenAnalyzed);

    return getCalls(matchCriteria, fields, pageable);
  }

  public RollupResultDTO averageCallFields(Criteria matchCriteria, List<CallField> fields)
      throws InvalidArgumentsException {
    List<AggregationField> aggregationFields = getFields(fields, false);
    MatchOperation matchOperation = match(matchCriteria);

    if (!isGroupable(aggregationFields)) {
      throw new InvalidArgumentsException("At least one valid groupable field is required.");
    }

    GroupOperation groupOperation =
        reduce(group(), aggregationFields, AggregationField::groupField);

    CustomAggregation<Call> aggregation = anAggregation().append(matchOperation);

    if (isProjectable(aggregationFields)) {
      aggregation.append(getFieldProjections(project(), aggregationFields));
    }

    aggregation.append(
        groupOperation,
        addFields().withAggregationFields("result", aggregationFields).build(),
        projectFields("result").build());

    RollupResultDTO resultDTO =
        executeSingleAggregation(aggregation.build(), RollupResultDTO.class);

    return resultDTO != null ? resultDTO : new RollupResultDTO(getDefaults(aggregationFields));
  }

  public RollupResultDTO averageCallFieldsByCompany(
      String companyId, DateTime startDate, DateTime endDate, List<CallField> fields)
      throws InvalidArgumentsException {
    Criteria matchCriteria =
        getCompanyConstrained(companyId)
            .andOperator(getDateConstrained(startDate, endDate), hasBeenAnalyzed);

    return averageCallFields(matchCriteria, fields);
  }

  public RollupResultDTO averageCallFieldsByUser(
      String userId, DateTime startDate, DateTime endDate, List<CallField> fields)
      throws InvalidArgumentsException {
    Criteria matchCriteria =
        getUserConstrained(userId)
            .andOperator(getDateConstrained(startDate, endDate), hasBeenAnalyzed);

    return averageCallFields(matchCriteria, fields);
  }

  public List<RollupResultDTO> rollupCallField(Criteria matchCriteria, List<CallField> fields)
      throws InvalidArgumentsException {
    return rollupCallField(matchCriteria, RollupCadence.DAILY, fields);
  }

  public List<RollupResultDTO> rollupCallField(
      Criteria matchCriteria, RollupCadence cadence, List<CallField> fields)
      throws InvalidArgumentsException {
    List<AggregationField> aggregationFields = getFields(fields, false);
    MatchOperation matchOperation = match(matchCriteria);

    if (!isGroupable(aggregationFields)) {
      throw new InvalidArgumentsException("At least one valid groupable field is required.");
    }

    ProjectionOperation projectionOperation =
        reduce(project(), aggregationFields, AggregationField::projectField);
    GroupOperation groupOperation =
        reduce(group("rollup"), aggregationFields, AggregationField::groupField);

    TypedAggregation<Call> aggregation =
        anAggregation()
            .append(
                matchOperation,
                projectionOperation
                    .and("createDate")
                    .dateAsFormattedString(cadence.getValue())
                    .as("rollup"),
                groupOperation,
                sort(Direction.ASC, previousOperation()),
                addFields().withAggregationFields("result", aggregationFields).build(),
                projectFields("result").and(TIMESTAMP).previousOperation().build())
            .build();

    return executeAggregation(aggregation, RollupResultDTO.class);
  }

  public List<RollupResultDTO> rollupCallFieldByCompany(
      String companyId,
      DateTime startDate,
      DateTime endDate,
      RollupCadence cadence,
      List<CallField> fields)
      throws InvalidArgumentsException {
    Criteria matchCriteria =
        getCompanyConstrained(companyId)
            .andOperator(getDateConstrained(startDate, endDate), hasBeenAnalyzed);

    return rollupCallField(matchCriteria, cadence, fields);
  }

  public List<RollupResultDTO> rollupCallFieldByUser(
      String userId,
      DateTime startDate,
      DateTime endDate,
      RollupCadence cadence,
      List<CallField> fields)
      throws InvalidArgumentsException {
    Criteria matchCriteria =
        getUserConstrained(userId)
            .andOperator(getDateConstrained(startDate, endDate), hasBeenAnalyzed);

    return rollupCallField(matchCriteria, cadence, fields);
  }
}
