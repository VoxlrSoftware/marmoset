package com.voxlr.marmoset.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.voxlr.marmoset.model.AuthUser;
import com.voxlr.marmoset.model.persistence.Call;
import com.voxlr.marmoset.model.persistence.dto.CallCreateDTO;
import com.voxlr.marmoset.repositories.CallRepository;

@Service
public class CallService {
    
    @Autowired
    private CallRepository callRepository;
    
    public Call create(CallCreateDTO callCreateDTO, AuthUser authUser) {
	
	return null;
    }

}
