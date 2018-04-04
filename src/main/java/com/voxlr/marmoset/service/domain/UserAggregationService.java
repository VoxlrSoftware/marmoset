package com.voxlr.marmoset.service.domain;

import com.voxlr.marmoset.aggregation.dto.UserAggregateDTO;
import com.voxlr.marmoset.aggregation.field.UserAggregationFields.UserField;
import com.voxlr.marmoset.model.AuthUser;
import com.voxlr.marmoset.model.dto.DateConstrained;
import com.voxlr.marmoset.model.persistence.Company;
import com.voxlr.marmoset.repositories.UserRepository;
import com.voxlr.marmoset.service.AggregationService;
import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class UserAggregationService extends AggregationService<UserField> {

  @Autowired private CompanyService companyService;
  @Autowired private UserRepository userRepository;

  @Override
  protected List<UserField> getFieldNames(List<String> fields) throws Exception {
    return super.getFieldNames(fields, UserField.class, UserField::getAll);
  }

  public Page<UserAggregateDTO> getUsersSummaryByCompany(
      ObjectId companyId,
      AuthUser authUser,
      DateConstrained dateConstrained,
      List<String> fields,
      Pageable pageable)
      throws Exception {
    validate(authUser, dateConstrained);
    Company company = companyService.get(companyId, authUser);
    List<UserField> userFields = getFieldNames(fields);
    return userRepository.getUsersSummaryByCompany(
        company.getId(),
        dateConstrained.getStartDate(),
        dateConstrained.getEndDate(),
        userFields,
        pageable);
  }
}
