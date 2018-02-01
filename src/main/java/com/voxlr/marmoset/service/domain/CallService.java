package com.voxlr.marmoset.service.domain;

import static com.voxlr.marmoset.model.persistence.factory.CallUpdate.anUpdate;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.exceptions.UnauthorizedUserException;
import org.springframework.stereotype.Service;

import com.voxlr.marmoset.model.AuthUser;
import com.voxlr.marmoset.model.CallOutcome;
import com.voxlr.marmoset.model.CallScoped;
import com.voxlr.marmoset.model.MongoService;
import com.voxlr.marmoset.model.persistence.Call;
import com.voxlr.marmoset.model.persistence.CallRequest;
import com.voxlr.marmoset.model.persistence.CallStrategy;
import com.voxlr.marmoset.model.persistence.dto.CallCreateDTO;
import com.voxlr.marmoset.model.persistence.dto.CallRequestCreateDTO;
import com.voxlr.marmoset.model.persistence.dto.CallUpdateDTO;
import com.voxlr.marmoset.model.persistence.factory.CallUpdate;
import com.voxlr.marmoset.repositories.CallRepository;
import com.voxlr.marmoset.repositories.CallRequestRepository;
import com.voxlr.marmoset.service.AuthorizationService;
import com.voxlr.marmoset.util.exception.EntityNotFoundException;
import com.voxlr.marmoset.util.exception.InvalidArgumentsException;

@Service
public class CallService {
    
    @Autowired
    private CompanyService companyService;
    
    @Autowired
    private AuthorizationService authorizationService;
    
    @Autowired
    private CallRepository callRepository;
    
    @Autowired
    private MongoService mongoService;
    
    @Autowired
    private CallRequestRepository callRequestRepository;
    
    @Autowired
    private ModelMapper modelMapper;
    
    public Call getByTranscriptionId(String transcriptionId) throws EntityNotFoundException {
	Call call = callRepository.findOneByTranscriptionId(transcriptionId);
	
	if (call == null) {
	    throw new EntityNotFoundException(Call.class, "transcriptionId", transcriptionId);
	}
	
	return call;
    }
    
    public Call getByCallSid(String callSid) throws EntityNotFoundException {
	Call call = callRepository.findOneByCallSid(callSid);
	
	if (call == null) {
	    throw new EntityNotFoundException(Call.class, "callSid", callSid);
	}
	
	return call;
    }
    
    public Call getInternal(String id) throws EntityNotFoundException {
	Call call = callRepository.findOne(id);
	
	if (call == null) {
	    throw new EntityNotFoundException(Call.class, "id", id);
	}
	
	return call;
    }
    
    public Call getInternal(CallScoped callScoped) throws EntityNotFoundException, InvalidArgumentsException {
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
	Call call = callRepository.findOne(id);
	
	if (call == null) {
	    throw new EntityNotFoundException(Call.class, "id", id);
	}
	
	if (!authorizationService.canRead(authUser, call)) {
	    throw new UnauthorizedUserException("Account unauthorized to view call");
	}
	
	return call;
    }
    
    private CallRequest getRequest(String requestId) throws EntityNotFoundException {
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
    
    public Call createFromRequest(String requestId, String callSid) throws Exception {
	CallRequest callRequest = getRequest(requestId);
	
	Call call = Call.builder()
		.callSid(callSid)
		.userId(callRequest.getUserId())
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
	
	if (callUpdateDTO.getCallOutcome() != null && CallOutcome.validateOutcome(callUpdateDTO.getCallOutcome())) {
	    update.withCallOutcome(callUpdateDTO.getCallOutcome());
	}
	
	call = mongoService.update(update);
	
	return call;
    }
    
    public Call updateInternal(CallUpdate update) {
	return mongoService.update(update);
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
