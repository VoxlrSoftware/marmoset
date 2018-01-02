package com.voxlr.marmoset.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.exceptions.UnauthorizedUserException;
import org.springframework.stereotype.Service;

import com.voxlr.marmoset.model.AuthUser;
import com.voxlr.marmoset.model.persistence.Call;
import com.voxlr.marmoset.model.persistence.dto.CallCreateDTO;
import com.voxlr.marmoset.model.persistence.dto.CallUpdateDTO;
import com.voxlr.marmoset.repositories.CallRepository;
import com.voxlr.marmoset.util.exception.EntityNotFoundException;

@Service
public class CallService {
    
    @Autowired
    private AuthorizationService authorizationService;
    
    @Autowired
    private CallRepository callRepository;
    
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
