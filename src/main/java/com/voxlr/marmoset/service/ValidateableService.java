package com.voxlr.marmoset.service;

import org.springframework.beans.factory.annotation.Autowired;

import com.voxlr.marmoset.model.AuthUser;

public abstract class ValidateableService {

    @Autowired
    private ValidationService validationService;
    
    public void validate(AuthUser authUser, Object entity) throws Exception {
	validationService.validate(authUser, entity);
    }
}
