package com.voxlr.marmoset.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.exceptions.UnauthorizedUserException;
import org.springframework.stereotype.Service;

import com.voxlr.marmoset.model.AuthUser;
import com.voxlr.marmoset.model.persistence.Call;
import com.voxlr.marmoset.model.persistence.dto.CallCreateDTO;
import com.voxlr.marmoset.repositories.CallRepository;

@Service
public class CallService {
    
    @Autowired
    private AuthorizationService authorizationService;
    
    @Autowired
    private CallRepository callRepository;
    
    @Autowired
    private ModelMapper modelMapper;
    
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

}
