package com.voxlr.marmoset.aggregation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.voxlr.marmoset.aggregation.dto.TotalCountDTO;
import com.voxlr.marmoset.model.ConvertibleEnum;
import com.voxlr.marmoset.model.persistence.AuditModel;
import lombok.RequiredArgsConstructor;
import org.joda.time.DateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static com.voxlr.marmoset.util.ListUtils.listOf;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@RequiredArgsConstructor
public abstract class AbstractAggregation<T> {

  public static enum RollupCadence implements ConvertibleEnum {
    HOURLY("hourly", "%Y-%m-%dT%H:00:00.000%z"),
    DAILY("daily", "%Y-%m-%dT00:00:00.000%z"),
    MONTHLY("monthly", "%Y-%m-01T00:00:00.000%z"),
    YEARLY("yearly", "%Y-01-01T00:00:00.000%z");

    static Map<String, RollupCadence> rollupCadences;

    static {
      rollupCadences =
          Arrays.asList(RollupCadence.values())
              .stream()
              .collect(Collectors.toMap(RollupCadence::getName, Function.identity()));
    }

    @JsonCreator
    public static RollupCadence fromString(String value) {
      return rollupCadences.get(value);
    }

    private String value;
    private String name;

    private RollupCadence(String name, String value) {
      this.name = name;
      this.value = value;
    }

    @JsonValue
    public String getName() {
      return this.name;
    }

    public String getValue() {
      return this.value;
    }
  }

  public static final String COUNT = "count";
  public static final String RESULT = "result";
  public static final String TIMESTAMP = "timestamp";

  public static Criteria getDateConstrained(DateTime startDate, DateTime endDate) {
    return Criteria.where(AuditModel.CREATE_DATE).gte(startDate).lte(endDate);
  }

  private final MongoTemplate mongoTemplate;
  private final Class<T> entityClass;

  public MongoTemplate mongo() {
    return mongoTemplate;
  }

  public GroupOperation count(GroupOperation group) {
    return group.count().as(COUNT);
  }

  public int doCount(MatchOperation matchOperation) {
    TypedAggregation<T> countAggregation =
        newAggregation(
            entityClass, matchOperation, group().count().as("totalCount"), project("totalCount"));

    AggregationResults<TotalCountDTO> totalCountResults =
        mongoTemplate.aggregate(countAggregation, TotalCountDTO.class);
    List<TotalCountDTO> results = totalCountResults.getMappedResults();

    return results.size() > 0 ? results.get(0).getTotalCount() : 0;
  }

  public <U> AggregationResults<U> getAggregationResults(
      TypedAggregation<T> aggregation, Class<U> outputClass) {
    return mongoTemplate.aggregate(aggregation, outputClass);
  }

  public <U> List<U> executeAggregation(TypedAggregation<T> aggregation, Class<U> outputClass) {
    return getAggregationResults(aggregation, outputClass).getMappedResults();
  }

  public <U> U executeSingleAggregation(TypedAggregation<T> aggregation, Class<U> outputClass) {
    AggregationResults<U> results = mongoTemplate.aggregate(aggregation, outputClass);

    if (results.getMappedResults().size() > 0) {
      return results.getMappedResults().get(0);
    }

    return null;
  }

  public <U> Page<U> executePagedAggregation(
      MatchOperation matchOperation,
      Pageable pageable,
      TypedAggregation<T> aggregation,
      Class<U> outputClass) {
    int totalCount = doCount(matchOperation);
    if (totalCount == 0) {
      return new PageImpl<>(newArrayList(), pageable, totalCount);
    }

    AggregationResults<U> results = mongoTemplate.aggregate(aggregation, outputClass);
    return new PageImpl<>(results.getMappedResults(), pageable, totalCount);
  }

  public CustomAggregation<T> anAggregation() {
    return new CustomAggregation<>(entityClass);
  }

  public static class CustomAggregation<T> {
    private List<AggregationOperation> operationList;
    private final Class<T> entityClass;

    private CustomAggregation(Class<T> entityClass) {
      this.entityClass = entityClass;
      operationList = newArrayList();
    }

    public TypedAggregation<T> build() {
      return newAggregation(entityClass, operationList);
    }

    public CustomAggregation<T> append(AggregationOperation... operations) {
      operationList.addAll(listOf(operations));
      return this;
    }

    public CustomAggregation<T> withPaging(Pageable pageable, SortOperation defaultSort) {
      SortOperation sortOperation = null;
      if (pageable.getSort().isSorted()) {
        sortOperation = sort(pageable.getSort());
      } else {
        sortOperation = sort(Direction.DESC, "createDate");
      }

      operationList.addAll(
          listOf(
              sortOperation,
              skip((long) pageable.getPageNumber() * pageable.getPageSize()),
              limit(pageable.getPageSize())));
      return this;
    }
  }
}
