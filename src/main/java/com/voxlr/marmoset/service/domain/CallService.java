package com.voxlr.marmoset.service.domain;

import com.voxlr.marmoset.aggregation.AbstractAggregation.RollupCadence;
import com.voxlr.marmoset.aggregation.dto.AggregateResultDTO;
import com.voxlr.marmoset.aggregation.dto.CallAggregateDTO;
import com.voxlr.marmoset.aggregation.dto.RollupResultDTO;
import com.voxlr.marmoset.aggregation.field.CallAggregationFields.CallField;
import com.voxlr.marmoset.convert.TypeConverter;
import com.voxlr.marmoset.exception.ConvertException;
import com.voxlr.marmoset.exception.EntityNotFoundException;
import com.voxlr.marmoset.exception.InvalidArgumentsException;
import com.voxlr.marmoset.model.AuthUser;
import com.voxlr.marmoset.model.CallOutcome;
import com.voxlr.marmoset.model.CallScoped;
import com.voxlr.marmoset.model.dto.DateConstrained;
import com.voxlr.marmoset.model.persistence.*;
import com.voxlr.marmoset.model.persistence.dto.CallCreateDTO;
import com.voxlr.marmoset.model.persistence.dto.CallRequestCreateDTO;
import com.voxlr.marmoset.model.persistence.dto.CallUpdateDTO;
import com.voxlr.marmoset.model.persistence.factory.CallUpdate;
import com.voxlr.marmoset.repositories.CallRepository;
import com.voxlr.marmoset.repositories.CallRequestRepository;
import com.voxlr.marmoset.service.AuthorizationService;
import com.voxlr.marmoset.service.ValidateableService;
import com.voxlr.marmoset.util.EnumUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.common.exceptions.UnauthorizedUserException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.voxlr.marmoset.model.persistence.factory.CallUpdate.anUpdate;

@Service
public class CallService extends ValidateableService {

  @Autowired private CompanyService companyService;

  @Autowired private UserService userService;

  @Autowired private AuthorizationService authorizationService;

  @Autowired private CallRepository callRepository;

  @Autowired private CallRequestRepository callRequestRepository;

  @Autowired private ModelMapper modelMapper;

  public Call getByTranscriptionId(String transcriptionId) throws EntityNotFoundException {
    Optional<Call> call = callRepository.findOneByTranscriptionId(transcriptionId);

    if (!call.isPresent()) {
      throw new EntityNotFoundException(Call.class, "transcriptionId", transcriptionId);
    }

    return call.get();
  }

  public Call getByCallSid(String callSid) throws EntityNotFoundException {
    Optional<Call> call = callRepository.findOneByCallSid(callSid);

    if (!call.isPresent()) {
      throw new EntityNotFoundException(Call.class, "callSid", callSid);
    }

    return call.get();
  }

  public Call getInternal(String id) throws EntityNotFoundException {
    Optional<Call> call = callRepository.findById(id);

    if (!call.isPresent()) {
      throw new EntityNotFoundException(Call.class, "id", id);
    }

    return call.get();
  }

  public Call getInternal(CallScoped callScoped)
      throws EntityNotFoundException, InvalidArgumentsException {
    Call call = null;

    if (callScoped.getId() != null) {
      call = getInternal(callScoped.getId());
    } else if (callScoped.getCallSid() != null) {
      call = getByCallSid(callScoped.getCallSid());
    }

    if (call == null) {
      throw new InvalidArgumentsException("Update requires an [id] or [callSid]");
    }

    return call;
  }

  public Call get(String id, AuthUser authUser) throws Exception {
    Call call = getInternal(id);

    if (!authorizationService.canRead(authUser, call)) {
      throw new UnauthorizedUserException("Account unauthorized to view call");
    }

    return call;
  }

  private CallRequest getRequest(String requestId) throws EntityNotFoundException {
    Optional<CallRequest> callRequest = callRequestRepository.findById(requestId);

    if (!callRequest.isPresent()) {
      throw new EntityNotFoundException(CallRequest.class, "id", requestId);
    }

    return callRequest.get();
  }

  public CallRequest createRequest(CallRequestCreateDTO callRequestCreateDTO, AuthUser authUser)
      throws EntityNotFoundException {
    if (!authorizationService.canCreate(authUser, Call.class)) {
      throw new UnauthorizedUserException("Account unauthorized to create call");
    }
    CallStrategy callStrategy =
        companyService.findCallStrategy(
            authUser.getCompanyId(), callRequestCreateDTO.getStrategyId());

    CallRequest request =
        CallRequest.builder()
            .employeeNumber(callRequestCreateDTO.getCallerId())
            .customerNumber(callRequestCreateDTO.getCustomerNumber())
            .userId(authUser.getId())
            .teamId(authUser.getTeamId())
            .companyId(authUser.getCompanyId())
            .callStrategy(callStrategy)
            .build();

    request = callRequestRepository.save(request);

    return request;
  }

  public Call createFromRequest(String requestId, String callSid) throws Exception {
    CallRequest callRequest = getRequest(requestId);

    Call call =
        Call.builder()
            .callSid(callSid)
            .userId(callRequest.getUserId())
            .teamId(callRequest.getTeamId())
            .companyId(callRequest.getCompanyId())
            .customerNumber(callRequest.getCustomerNumber())
            .employeeNumber(callRequest.getEmployeeNumber())
            .callStrategy(callRequest.getCallStrategy())
            .build();

    callRequestRepository.delete(callRequest);
    call = callRepository.save(call);
    return call;
  }

  public Call create(CallCreateDTO callCreateDTO, AuthUser authUser) {
    if (!authorizationService.canCreate(authUser, Call.class)) {
      throw new UnauthorizedUserException("Account unauthorized to create call");
    }

    Call call = modelMapper.map(callCreateDTO, Call.class);
    call.setUserId(authUser.getId());
    call.setCompanyId(authUser.getCompanyId());

    call = callRepository.save(call);
    return call;
  }

  public Call update(CallUpdateDTO callUpdateDTO, AuthUser authUser) throws Exception {
    Call call = getInternal(callUpdateDTO);

    if (!authorizationService.canWrite(authUser, call)) {
      throw new UnauthorizedUserException("Account unauthorized to view call.");
    }

    CallUpdate update = anUpdate(call);

    if (callUpdateDTO.getCallOutcome() != null
        && CallOutcome.validateOutcome(callUpdateDTO.getCallOutcome())) {
      update.withCallOutcome(callUpdateDTO.getCallOutcome());
    }

    call = callRepository.update(update);

    return call;
  }

  public Call updateInternal(CallUpdate update) {
    return callRepository.update(update);
  }

  public void delete(String id, AuthUser authUser) throws Exception {
    Call call = getInternal(id);

    if (!authorizationService.canWrite(authUser, call)) {
      throw new UnauthorizedUserException("Account unauthorized to delete call");
    }

    callRepository.delete(call);
  }

  public Page<CallAggregateDTO> getCallsByCompanyId(
      String companyId,
      AuthUser authUser,
      DateConstrained dateConstrained,
      List<String> fields,
      Pageable pageable)
      throws Exception {
    validate(authUser, dateConstrained);
    Company company = companyService.get(companyId, authUser);
    List<CallField> callFields = getCallFields(fields);
    return callRepository.getCallsByCompany(
        company.getId(),
        dateConstrained.getStartDate(),
        dateConstrained.getEndDate(),
        callFields,
        pageable);
  }

  public Page<CallAggregateDTO> getCallsByUserId(
      String userId,
      AuthUser authUser,
      DateConstrained dateConstrained,
      List<String> fields,
      Pageable pageable)
      throws Exception {
    validate(authUser, dateConstrained);
    User user = userService.get(userId, authUser);
    List<CallField> callFields = getCallFields(fields);
    return callRepository.getCallsByUser(
        user.getId(),
        dateConstrained.getStartDate(),
        dateConstrained.getEndDate(),
        callFields,
        pageable);
  }

  public RollupResultDTO averageCallsByCompanyId(
      String companyId, AuthUser authUser, DateConstrained dateConstrained, List<String> fields)
      throws Exception {
    validate(authUser, dateConstrained);
    Company company = companyService.get(companyId, authUser);
    List<CallField> callFields = getCallFields(fields);
    return callRepository.averageCallFieldByCompany(
        company.getId(), dateConstrained.getStartDate(), dateConstrained.getEndDate(), callFields);
  }

  public List<RollupResultDTO> rollupCallsByCompanyId(
      String companyId,
      AuthUser authUser,
      DateConstrained dateConstrained,
      String cadence,
      List<String> fields)
      throws Exception {
    validate(authUser, dateConstrained);
    Company company = companyService.get(companyId, authUser);
    List<CallField> callFields = getCallFields(fields);
    return callRepository.rollupCallFieldByCompany(
        company.getId(),
        dateConstrained.getStartDate(),
        dateConstrained.getEndDate(),
        getRollupCadence(cadence),
        callFields);
  }

  public RollupResultDTO averageCallsByUserId(
      String userId, AuthUser authUser, DateConstrained dateConstrained, List<String> fields)
      throws Exception {
    validate(authUser, dateConstrained);
    User user = userService.get(userId, authUser);
    List<CallField> callFields = getCallFields(fields);
    return callRepository.averageCallFieldByUser(
        user.getId(), dateConstrained.getStartDate(), dateConstrained.getEndDate(), callFields);
  }

  public List<RollupResultDTO> rollupCallsByUserId(
      String userId,
      AuthUser authUser,
      DateConstrained dateConstrained,
      String cadence,
      List<String> fields)
      throws Exception {
    validate(authUser, dateConstrained);
    User user = userService.get(userId, authUser);
    List<CallField> callFields = getCallFields(fields);
    RollupCadence rollupCadence = getRollupCadence(cadence);
    return callRepository.rollupCallFieldByUser(
        user.getId(),
        dateConstrained.getStartDate(),
        dateConstrained.getEndDate(),
        rollupCadence,
        callFields);
  }

  public AggregateResultDTO getCallOutcomesByCompany(
      String companyId, AuthUser authUser, DateConstrained dateConstrained) throws Exception {
    validate(authUser, dateConstrained);
    Company company = companyService.get(companyId, authUser);
    return callRepository.getCallOutcomesByCompany(
        company.getId(), dateConstrained.getStartDate(), dateConstrained.getEndDate());
  }

  public AggregateResultDTO getCallOutcomesByUser(
      String userId, AuthUser authUser, DateConstrained dateConstrained) throws Exception {
    validate(authUser, dateConstrained);
    User user = userService.get(userId, authUser);
    return callRepository.getCallOutcomesByUser(
        user.getId(), dateConstrained.getStartDate(), dateConstrained.getEndDate());
  }

  private List<CallField> getCallFields(List<String> fields) throws Exception {
    if (fields.size() == 1 && fields.get(0).equalsIgnoreCase("true")) {
      return CallField.getAll();
    }

    try {
      return TypeConverter.convertList(fields, CallField.class, EnumUtils::convert);
    } catch (ConvertException e) {
      throw new InvalidArgumentsException(
          "Invalid arguments for param fields: ["
              + String.join(",", e.getNonConvertableStrings())
              + "]");
    }
  }

  private RollupCadence getRollupCadence(String cadence) throws Exception {
    try {
      return TypeConverter.convert(cadence, RollupCadence.class, EnumUtils::convert);
    } catch (ConvertException e) {
      throw new InvalidArgumentsException(
          "Invalid arguments for param fields: ["
              + String.join(",", e.getNonConvertableStrings())
              + "]");
    }
  }
}
