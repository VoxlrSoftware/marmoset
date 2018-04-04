package com.voxlr.marmoset.service.domain;

import com.voxlr.marmoset.aggregation.AbstractAggregation.RollupCadence;
import com.voxlr.marmoset.aggregation.dto.AggregateResultDTO;
import com.voxlr.marmoset.aggregation.dto.CallAggregateDTO;
import com.voxlr.marmoset.aggregation.dto.RollupResultDTO;
import com.voxlr.marmoset.aggregation.field.CallAggregationFields.CallField;
import com.voxlr.marmoset.model.AuthUser;
import com.voxlr.marmoset.model.dto.DateConstrained;
import com.voxlr.marmoset.model.persistence.Company;
import com.voxlr.marmoset.model.persistence.User;
import com.voxlr.marmoset.repositories.CallRepository;
import com.voxlr.marmoset.service.AggregationService;
import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CallAggregationService extends AggregationService<CallField> {

  @Autowired private CompanyService companyService;
  @Autowired private UserService userService;
  @Autowired private CallRepository callRepository;

  public Page<CallAggregateDTO> getCallsByCompanyId(
      ObjectId companyId,
      AuthUser authUser,
      DateConstrained dateConstrained,
      List<String> fields,
      Pageable pageable)
      throws Exception {
    validate(authUser, dateConstrained);
    Company company = companyService.get(companyId, authUser);
    List<CallField> callFields = getFieldNames(fields);
    return callRepository.getCallsByCompany(
        company.getId(),
        dateConstrained.getStartDate(),
        dateConstrained.getEndDate(),
        callFields,
        pageable);
  }

  public Page<CallAggregateDTO> getCallsByUserId(
      ObjectId userId,
      AuthUser authUser,
      DateConstrained dateConstrained,
      List<String> fields,
      Pageable pageable)
      throws Exception {
    validate(authUser, dateConstrained);
    User user = userService.get(userId, authUser);
    List<CallField> callFields = getFieldNames(fields);
    return callRepository.getCallsByUser(
        user.getId(),
        dateConstrained.getStartDate(),
        dateConstrained.getEndDate(),
        callFields,
        pageable);
  }

  public RollupResultDTO averageCallsByCompanyId(
      ObjectId companyId, AuthUser authUser, DateConstrained dateConstrained, List<String> fields)
      throws Exception {
    validate(authUser, dateConstrained);
    Company company = companyService.get(companyId, authUser);
    List<CallField> callFields = getFieldNames(fields);
    return callRepository.averageCallFieldByCompany(
        company.getId(), dateConstrained.getStartDate(), dateConstrained.getEndDate(), callFields);
  }

  public List<RollupResultDTO> rollupCallsByCompanyId(
      ObjectId companyId,
      AuthUser authUser,
      DateConstrained dateConstrained,
      String cadence,
      List<String> fields)
      throws Exception {
    validate(authUser, dateConstrained);
    Company company = companyService.get(companyId, authUser);
    List<CallField> callFields = getFieldNames(fields);
    return callRepository.rollupCallFieldByCompany(
        company.getId(),
        dateConstrained.getStartDate(),
        dateConstrained.getEndDate(),
        getRollupCadence(cadence),
        callFields);
  }

  public RollupResultDTO averageCallsByUserId(
      ObjectId userId, AuthUser authUser, DateConstrained dateConstrained, List<String> fields)
      throws Exception {
    validate(authUser, dateConstrained);
    User user = userService.get(userId, authUser);
    List<CallField> callFields = getFieldNames(fields);
    return callRepository.averageCallFieldByUser(
        user.getId(), dateConstrained.getStartDate(), dateConstrained.getEndDate(), callFields);
  }

  public List<RollupResultDTO> rollupCallsByUserId(
      ObjectId userId,
      AuthUser authUser,
      DateConstrained dateConstrained,
      String cadence,
      List<String> fields)
      throws Exception {
    validate(authUser, dateConstrained);
    User user = userService.get(userId, authUser);
    List<CallField> callFields = getFieldNames(fields);
    RollupCadence rollupCadence = getRollupCadence(cadence);
    return callRepository.rollupCallFieldByUser(
        user.getId(),
        dateConstrained.getStartDate(),
        dateConstrained.getEndDate(),
        rollupCadence,
        callFields);
  }

  public AggregateResultDTO getCallOutcomesByCompany(
      ObjectId companyId, AuthUser authUser, DateConstrained dateConstrained) throws Exception {
    validate(authUser, dateConstrained);
    Company company = companyService.get(companyId, authUser);
    return callRepository.getCallOutcomesByCompany(
        company.getId(), dateConstrained.getStartDate(), dateConstrained.getEndDate());
  }

  public AggregateResultDTO getCallOutcomesByUser(
      ObjectId userId, AuthUser authUser, DateConstrained dateConstrained) throws Exception {
    validate(authUser, dateConstrained);
    User user = userService.get(userId, authUser);
    return callRepository.getCallOutcomesByUser(
        user.getId(), dateConstrained.getStartDate(), dateConstrained.getEndDate());
  }

  @Override
  protected List<CallField> getFieldNames(List<String> fields) throws Exception {
    return super.getFieldNames(fields, CallField.class, CallField::getAll);
  }
}
