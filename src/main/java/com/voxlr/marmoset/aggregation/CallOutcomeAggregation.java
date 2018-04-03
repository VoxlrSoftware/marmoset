package com.voxlr.marmoset.aggregation;

import static com.voxlr.marmoset.util.ListUtils.reduce;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;

import com.voxlr.marmoset.aggregation.dto.AggregateResultDTO;
import com.voxlr.marmoset.aggregation.field.AggregationField;
import com.voxlr.marmoset.aggregation.field.CallAggregationFields;
import com.voxlr.marmoset.aggregation.field.CallAggregationFields.CallField;
import com.voxlr.marmoset.model.persistence.Call;
import java.util.List;
import java.util.function.Function;
import org.joda.time.DateTime;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;

public class CallOutcomeAggregation extends AbstractAggregation<Call, CallField> {

  private final AggregationField CALL_OUTCOME = CallAggregationFields.field(CallField.CALL_OUTCOME);

  public CallOutcomeAggregation(MongoTemplate mongoTemplate) {
    super(mongoTemplate, Call.class);
  }

  private AggregateResultDTO reduceCounts(List<AggregateResultDTO> resultDTO) {
    return reduce(new AggregateResultDTO(), resultDTO, (results, result) -> {
      results.put(result.get(ID).toString(), result.get(COUNT));
      return results;
    });
  }

  public AggregateResultDTO getCallOutcomes(Criteria matchCriteria) {
    MatchOperation matchOperation = match(matchCriteria);
    TypedAggregation<Call> aggregation =
        anAggregation().append(
                matchOperation,
                count(group(CALL_OUTCOME.getFieldName()))
        ).build();

    List<AggregateResultDTO> resultDTO = executeAggregation(aggregation, AggregateResultDTO.class);
    return reduceCounts(resultDTO);
  }

  public AggregateResultDTO getCallOutcomesByUser(
      String userId, DateTime startDate, DateTime endDate) {
    Criteria matchCriteria =
        CallAggregation.getUserConstrained(userId)
            .andOperator(getDateConstrained(startDate, endDate));

    return getCallOutcomes(matchCriteria);
  }

  public AggregateResultDTO getCallOutcomesByCompany(
      String companyId, DateTime startDate, DateTime endDate) {
    Criteria matchCriteria =
        CallAggregation.getCompanyConstrained(companyId)
            .andOperator(getDateConstrained(startDate, endDate));

    return getCallOutcomes(matchCriteria);
  }

  @Override
  public Function<CallField, AggregationField> getMapper() {
    return CallAggregationFields::field;
  }
}
