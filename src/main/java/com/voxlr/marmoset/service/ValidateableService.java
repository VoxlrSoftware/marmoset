package com.voxlr.marmoset.service;

import org.springframework.beans.factory.annotation.Autowired;

import com.voxlr.marmoset.model.AuthUser;
import com.voxlr.marmoset.util.exception.InvalidArgumentsException;

public abstract class ValidateableService {

    @Autowired
    private ValidationService validationService;
    
    public void validate(AuthUser authUser, Object entity) throws Exception {
	validationService.validate(authUser, entity);
    }
    
    public void validateNotNull(Object obj, String fieldName) throws InvalidArgumentsException {
	if (obj == null) {
	    throw new InvalidArgumentsException("Invalid value for field [" + fieldName + "]");
	}
    }
}
