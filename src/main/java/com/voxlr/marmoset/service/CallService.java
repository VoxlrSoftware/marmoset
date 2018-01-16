package com.voxlr.marmoset.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.exceptions.UnauthorizedUserException;
import org.springframework.stereotype.Service;

import com.voxlr.marmoset.model.AuthUser;
import com.voxlr.marmoset.model.persistence.Call;
import com.voxlr.marmoset.model.persistence.CallRequest;
import com.voxlr.marmoset.model.persistence.CallStrategy;
import com.voxlr.marmoset.model.persistence.User;
import com.voxlr.marmoset.model.persistence.dto.CallCreateDTO;
import com.voxlr.marmoset.model.persistence.dto.CallRequestCreateDTO;
import com.voxlr.marmoset.model.persistence.dto.CallUpdateDTO;
import com.voxlr.marmoset.repositories.CallRepository;
import com.voxlr.marmoset.repositories.CallRequestRepository;
import com.voxlr.marmoset.util.exception.EntityNotFoundException;

@Service
public class CallService {
    
    @Autowired
    private CompanyService companyService;
    
    @Autowired
    private AuthorizationService authorizationService;
    
    @Autowired
    private CallRepository callRepository;
    
    @Autowired
    private CallRequestRepository callRequestRepository;
    
    @Autowired
    private ModelMapper modelMapper;
    
    public Call get(String id, AuthUser authUser) throws Exception {
	Call call = callRepository.findOne(id);
	
	if (call == null) {
	    throw new EntityNotFoundException(Call.class, "id", id);
	}
	
	if (!authorizationService.canRead(authUser, call)) {
	    throw new UnauthorizedUserException("Account unauthorized to view call");
	}
	
	return call;
    }
    
    public CallRequest getRequest(String requestId) throws EntityNotFoundException {
	CallRequest callRequest = callRequestRepository.findOne(requestId);
	
	if (callRequestRepository == null) {
	    throw new EntityNotFoundException(CallRequest.class, "id", requestId);
	}
	
	return callRequest;
    }
    
    public CallRequest createRequest(CallRequestCreateDTO callRequestCreateDTO, AuthUser authUser) throws EntityNotFoundException {
	if (!authorizationService.canCreate(authUser, Call.class)) {
	    throw new UnauthorizedUserException("Account unauthorized to create call");
	}
	CallStrategy callStrategy = companyService.findCallStrategy(authUser.getCompanyId(),
		callRequestCreateDTO.getStrategyId());
	
	CallRequest request = CallRequest.builder()
		.employeeNumber(callRequestCreateDTO.getCallerId())
		.customerNumber(callRequestCreateDTO.getCustomerNumber())
		.userId(authUser.getId())
		.companyId(authUser.getCompanyId())
		.callStrategy(callStrategy)
		.build();
	
	request = callRequestRepository.save(request);
	
	return request;
    }
    
    public Call create(CallRequest callRequest, String callSid) throws Exception {
	if (callRequest == null) {
	    throw new Exception("CallRequest must be valid.");
	}
	
	Call call = Call.builder()
		.callSid(callSid)
		.userId(callRequest.getUserId())
		.companyId(callRequest.getCompanyId())
		.customerNumber(callRequest.getCustomerNumber())
		.employeeNumber(callRequest.getEmployeeNumber())
		.build();
	
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
	Call call = callRepository.findOne(callUpdateDTO.getId());
	
	if (call == null) {
	    throw new EntityNotFoundException(Call.class, "id", callUpdateDTO.getId());
	}
	
	if (!authorizationService.canWrite(authUser, call)) {
	    throw new UnauthorizedUserException("Account unauthorized to view call.");
	}
	
	if (callUpdateDTO.getCallOutcome() != null) {
	    call.setCallOutcome(callUpdateDTO.getCallOutcome());
	}
	
	return call;
    }
    
    public void delete(String id, AuthUser authUser) throws Exception {
	Call call = callRepository.findOne(id);
	
	if (call == null) {
	    throw new EntityNotFoundException(Call.class, "id", id);
	}
	
	if (!authorizationService.canWrite(authUser, call)) {
	    throw new UnauthorizedUserException("Account unauthorized to delete call");
	}
	
	callRepository.delete(call);
    }

}
