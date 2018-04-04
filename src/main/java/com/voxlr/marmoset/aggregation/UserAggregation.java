package com.voxlr.marmoset.aggregation;

import static com.voxlr.marmoset.aggregation.field.UserAggregationFields.field;
import static com.voxlr.marmoset.util.ListUtils.listOf;
import static com.voxlr.marmoset.util.ListUtils.reduce;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;

import com.voxlr.marmoset.aggregation.dto.UserAggregateDTO;
import com.voxlr.marmoset.aggregation.field.AggregationField;
import com.voxlr.marmoset.aggregation.field.UserAggregationFields;
import com.voxlr.marmoset.aggregation.field.UserAggregationFields.UserField;
import com.voxlr.marmoset.model.persistence.User;
import java.util.List;
import java.util.function.Function;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;

public class UserAggregation extends AbstractAggregation<User, UserField> {

  public static UserAggregation aUserAggregation(MongoTemplate mongoTemplate) {
    return new UserAggregation(mongoTemplate);
  }

  public UserAggregation(MongoTemplate mongoTemplate) {
    super(
        mongoTemplate,
        User.class,
        listOf(
            field(UserField.COMPANY_ID),
            field(UserField.LAST_NAME)
        ));
  }

  public static Criteria getCompanyConstrained(ObjectId companyId) {
    return Criteria.where(UserField.COMPANY_ID.getName()).is(companyId);
  }

  public Function<UserField, AggregationField> getMapper() {
    return UserAggregationFields::field;
  }

  private Page<UserAggregateDTO> getUserSummary(
      Criteria matchCriteria, List<UserField> fields, Pageable pageable) {
    MatchOperation matchOperation = match(matchCriteria);
    ProjectionOperation projectOperation = reduce(project(), getFields(fields), AggregationField::projectField);
    TypedAggregation<User> aggregation =
        anAggregation()
          .append(matchOperation)
        .withPaging(pageable, sort(Direction.DESC, "lastName"))
        .append(projectOperation)
        .build();

    return executePagedAggregation(matchOperation, pageable, aggregation, UserAggregateDTO.class);
  }

  public Page<UserAggregateDTO> getUsersByCompany(ObjectId companyId, DateTime startDate, DateTime endDate, List<UserField> fields, Pageable pageable) {
    Criteria matchCriteria = getCompanyConstrained(companyId).andOperator(getDateConstrained(startDate, endDate));
    return getUserSummary(matchCriteria, fields, pageable);
  }

}
