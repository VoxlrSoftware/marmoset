package com.voxlr.marmoset.repositories;

import static com.voxlr.marmoset.aggregation.UserAggregation.aUserAggregation;

import com.voxlr.marmoset.aggregation.dto.UserAggregateDTO;
import com.voxlr.marmoset.aggregation.field.UserAggregationFields.UserField;
import java.util.List;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;

public class CustomizedUserRepositoryImpl implements CustomizedUserRepository {

  @Autowired
  private MongoTemplate mongoTemplate;

  @Override
  public Page<UserAggregateDTO> getUsersSummaryByCompany(String companyId, DateTime startDate,
      DateTime endDate, List<UserField> fields, Pageable pageable) {
    return aUserAggregation(mongoTemplate)
        .getUsersByCompany(companyId, startDate, endDate, fields, pageable);
  }
}
