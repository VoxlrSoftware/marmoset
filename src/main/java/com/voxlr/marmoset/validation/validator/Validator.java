package com.voxlr.marmoset.validation.validator;

import com.voxlr.marmoset.model.AuthUser;

public interface Validator<T> {
    
    void validate(AuthUser authUser, T entity) throws Exception;
}
