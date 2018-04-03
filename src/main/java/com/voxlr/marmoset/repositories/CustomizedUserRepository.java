package com.voxlr.marmoset.repositories;

import com.voxlr.marmoset.aggregation.dto.UserAggregateDTO;
import com.voxlr.marmoset.aggregation.field.UserAggregationFields.UserField;
import java.util.List;
import org.joda.time.DateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomizedUserRepository {
  Page<UserAggregateDTO> getUsersSummaryByCompany(
      String companyId,
      DateTime startDate,
      DateTime endDate,
      List<UserField> fields,
      Pageable pageable);
}
