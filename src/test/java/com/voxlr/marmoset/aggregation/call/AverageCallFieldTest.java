package com.voxlr.marmoset.aggregation.call;

import com.voxlr.marmoset.aggregation.CallAggregation;
import com.voxlr.marmoset.aggregation.dto.RollupResultDTO;
import com.voxlr.marmoset.aggregation.field.AggregationField;
import com.voxlr.marmoset.aggregation.field.CallAggregationFields;
import com.voxlr.marmoset.aggregation.field.CallAggregationFields.CallField;
import com.voxlr.marmoset.exception.InvalidArgumentsException;
import com.voxlr.marmoset.model.persistence.Call;
import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.voxlr.marmoset.util.AssertUtils.wrapAssertException;
import static com.voxlr.marmoset.util.AssertUtils.wrapNoException;
import static com.voxlr.marmoset.util.ListUtils.listOf;
import static com.voxlr.marmoset.util.MatcherUtils.anyObjectId;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsMapContaining.hasKey;
import static org.junit.Assert.assertThat;

public class AverageCallFieldTest extends CallAggregationBaseTest {
  @Test
  public void averageCallFieldReturnsValidDefaultValue() throws Exception {
    Map<CallField, AggregationField> groupableFields = CallAggregationFields.getGroupableFields();

    RollupResultDTO resultDTO =
        callAggregation.averageCallFields(
            Criteria.where("id")
                .exists(true)
                .andOperator(CallAggregation.getDateConstrained(new DateTime(), new DateTime())),
            new ArrayList<>(groupableFields.keySet()));

    assertThat(resultDTO.getResult().size(), is(groupableFields.size()));

    Map<String, Object> result = resultDTO.getResult();

    groupableFields
        .values()
        .stream()
        .forEach(
            field -> {
              assertThat(result, hasKey(field.getFieldName()));
              assertThat(result.get(field.getFieldName()), is(field.getDefaultValue()));
            });
  }

  @Test
  public void averageCallFieldReturnsValidValue() throws InvalidArgumentsException {
    DateTime startDate = new DateTime().minusDays(7);
    DateTime endDate = new DateTime().minusDays(1);

    Call call1 = createCall(startDate.plusDays(1));
    Call call2 = createCall(startDate.plusDays(1));
    call2.getStatistics().setTotalTalkTime(2000);
    persistenceUtils.save(call1, call2);

    RollupResultDTO resultDTO =
        callAggregation.averageCallFields(
            Criteria.where("id")
                .exists(true)
                .andOperator(CallAggregation.getDateConstrained(startDate, endDate)),
            listOf(CallField.TOTAL_TALK_TIME));

    assertThat(resultDTO.getResult().get(CallField.TOTAL_TALK_TIME.getName()), equalTo(6000.0));
  }

  @Test
  public void averageCallFieldIgnoresOutOfDateRange() throws InvalidArgumentsException {
    DateTime startDate = new DateTime().minusDays(7);
    DateTime endDate = new DateTime().minusDays(1);

    Call call1 = createCall(startDate.plusDays(1));
    Call call2 = createCall(startDate.minusDays(1));
    call2.getStatistics().setTotalTalkTime(2000);
    persistenceUtils.save(call1, call2);

    RollupResultDTO resultDTO =
        callAggregation.averageCallFields(
            Criteria.where("id")
                .exists(true)
                .andOperator(CallAggregation.getDateConstrained(startDate, endDate)),
            listOf(CallField.TOTAL_TALK_TIME));

    assertThat(resultDTO.getResult().get(CallField.TOTAL_TALK_TIME.getName()), equalTo(10000.0));
  }

  @Test
  public void averageCallFieldAcceptsValidField() {
    DateTime startDate = new DateTime().minusDays(7);
    DateTime endDate = new DateTime().minusDays(1);

    Call call = createCall(startDate.plusDays(1));
    persistenceUtils.save(call);

    CallAggregationFields.getGroupableFields()
        .keySet()
        .stream()
        .forEach(
            field -> {
              wrapNoException(
                  () -> {
                    callAggregation.averageCallFields(
                        Criteria.where("id")
                            .exists(true)
                            .andOperator(CallAggregation.getDateConstrained(startDate, endDate)),
                        listOf(field));
                  });
            });
  }

  @Test
  public void averageCallFieldThrowsInvalidField() {
    DateTime startDate = new DateTime().minusDays(7);
    DateTime endDate = new DateTime().minusDays(1);

    Call call = createCall(startDate.plusDays(1));
    persistenceUtils.save(call);

    Map<CallField, AggregationField> aggregationFields = CallAggregationFields.getFields();
    List<CallField> nonGroupableFields =
        aggregationFields
            .keySet()
            .stream()
            .filter(field -> !aggregationFields.get(field).isAbleToRollup())
            .collect(Collectors.toList());

    nonGroupableFields
        .stream()
        .forEach(
            field -> {
              wrapAssertException(
                  () -> {
                    callAggregation.averageCallFields(
                        Criteria.where("id")
                            .exists(true)
                            .andOperator(CallAggregation.getDateConstrained(startDate, endDate)),
                        listOf(field));
                  },
                  InvalidArgumentsException.class);
            });
  }

  @Test
  public void averageCallFieldByCompanyOnlyAveragesSameCompany() throws InvalidArgumentsException {
    DateTime startDate = new DateTime().minusDays(7);
    DateTime endDate = new DateTime().minusDays(1);

    Call call1 = createCall(startDate.plusDays(1));
    Call call2 = createCall(startDate.plusDays(1));
    call2.setCompanyId(anyObjectId());
    persistenceUtils.save(call1, call2);

    RollupResultDTO resultDTO =
        callAggregation.averageCallFieldsByCompany(
            mockCompany.getId(), startDate, endDate, listOf(CallField.TOTAL_TALK_TIME));

    assertThat(resultDTO.getResult().get(CallField.TOTAL_TALK_TIME.getName()), equalTo(10000.0));
  }

  @Test
  public void averageCallFieldByUserOnlyAveragesSameUser() throws InvalidArgumentsException {
    DateTime startDate = new DateTime().minusDays(7);
    DateTime endDate = new DateTime().minusDays(1);

    Call call1 = createCall(startDate.plusDays(1));
    Call call2 = createCall(startDate.plusDays(1));
    call2.setUserId(anyObjectId());
    persistenceUtils.save(call1, call2);

    RollupResultDTO resultDTO =
        callAggregation.averageCallFieldsByUser(
            mockUser.getId(), startDate, endDate, listOf(CallField.TOTAL_TALK_TIME));

    assertThat(resultDTO.getResult().get(CallField.TOTAL_TALK_TIME.getName()), equalTo(10000.0));
  }
}
