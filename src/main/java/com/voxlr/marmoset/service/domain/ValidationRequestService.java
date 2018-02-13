package com.voxlr.marmoset.service.domain;

import static com.voxlr.marmoset.model.PhoneNumberHolder.comparePhoneNumbers;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.oauth2.common.exceptions.UnauthorizedUserException;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.twilio.exception.ApiException;
import com.voxlr.marmoset.model.AuthUser;
import com.voxlr.marmoset.model.PhoneNumberHolder;
import com.voxlr.marmoset.model.Phoneable;
import com.voxlr.marmoset.model.persistence.Call;
import com.voxlr.marmoset.model.persistence.ValidationRequest;
import com.voxlr.marmoset.model.persistence.dto.ValidatePhoneResponseDTO;
import com.voxlr.marmoset.repositories.ValidationRequestRepository;
import com.voxlr.marmoset.service.AuthorizationService;
import com.voxlr.marmoset.service.TwilioService;
import com.voxlr.marmoset.util.exception.EntityNotFoundException;

import lombok.Getter;

@Service
public class ValidationRequestService {
    private enum ValidationError {
	ALREADY_VERIFIED("Phone number is already verified.");
	
	@Getter
	private String message;
	private ValidationError(String message) {
	    this.message = message;
	}
    }
    
    public static enum ValidationType {
	    @JsonProperty("company") COMPANY,
	    @JsonProperty("user") USER
    };
    
    @Autowired
    private ValidationRequestRepository validationRequestRepository;
    
    @Autowired
    private TwilioService twilioService;
    
    @Autowired
    private AuthorizationService authorizationService;
    
    @Autowired
    private ModelMapper modelMapper;
    
    @Autowired
    private MongoTemplate mongoTemplate;
    
    public ValidatePhoneResponseDTO validatePhoneNumber(
	    AuthUser authUser,
	    Phoneable<?> entity,
	    PhoneNumberHolder phoneNumber) {
	ValidatePhoneResponseDTO responseDTO;
	
	if (comparePhoneNumbers(entity.getPhoneNumber(), phoneNumber)) {
	    responseDTO = ValidatePhoneResponseDTO.buildValidResponse();
	} else {
	    try {
		com.twilio.rest.api.v2010.account.ValidationRequest twilioValidationRequest = twilioService.validatePhoneNumber(phoneNumber);
		ValidationRequest request = createRequest(
			authUser,
			entity,
			phoneNumber,
			twilioValidationRequest.getCallSid());
		responseDTO = modelMapper.map(request, ValidatePhoneResponseDTO.class);
		responseDTO.setValidationCode(twilioValidationRequest.getValidationCode());
	    } catch (ApiException ex) {
		if (ex.getMessage().equals(ValidationError.ALREADY_VERIFIED.getMessage())) {
		    updatePhoneNumberForEntity(entity, phoneNumber);
		    responseDTO = ValidatePhoneResponseDTO.buildValidResponse();
		} else {
		    throw ex;
		}
	    }
	}
	
	return responseDTO;
    }
    
    public ValidationRequest getRequest(String id, AuthUser authUser) throws EntityNotFoundException {
	Optional<ValidationRequest> request = validationRequestRepository.findById(id);
	
	if (!request.isPresent()) {
	    throw new EntityNotFoundException(Call.class, "id", id);
	}
	
	if (!authorizationService.canRead(authUser, request.get())) {
	    throw new UnauthorizedUserException("Unauthorized to view validation request.");
	}
	
	return request.get();
    }
    
    public ValidationRequest createRequest(
	    AuthUser authUser,
	    Phoneable<?> entity,
	    PhoneNumberHolder phoneNumber,
	    String requestId) {
	ValidationRequest request = ValidationRequest.builder()
		.entityId(entity.getId())
		.userId(authUser.getId())
		.entityType(entity.getClass().getTypeName())
		.phoneNumber(phoneNumber)
		.requestId(requestId)
		.build();
	
	ValidationRequest currentRequest = validationRequestRepository.locateRequest(authUser.getId(), entity.getId());
	if (currentRequest != null) {
	    validationRequestRepository.delete(currentRequest);
	}
	
	request = validationRequestRepository.save(request);
	return request;
    }
    
    public void updatePhoneNumberForEntity(Phoneable<?> entity, PhoneNumberHolder phoneNumber) {
	entity.setPhoneNumber(phoneNumber);
	mongoTemplate.save(entity);
    }
    
    public void handleValidationResponse(String requestId, boolean response) {
	ValidationRequest request = validationRequestRepository.findOneByRequestId(requestId);
	
	if (request != null) {
	    request.setHasValidated(true);
	    if (response) {		
		try {
		    Class<?> entityType = Class.forName(request.getEntityType());
		    Phoneable<?> entity = (Phoneable<?>) mongoTemplate.findById(request.getEntityId(), entityType);
		    
		    if (entity != null) {
			updatePhoneNumberForEntity(entity, request.getPhoneNumber());
			request.setValid(true);
		    }
		} catch (Exception e) {}
	    }
	    validationRequestRepository.save(request);
	}
    }
    
    public void setResult(ValidationRequest validationRequest, boolean result) {
	validationRequest.setHasValidated(true);
	validationRequest.setValid(result);
	validationRequestRepository.save(validationRequest);
    }
}
