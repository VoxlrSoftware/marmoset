package com.voxlr.marmoset.validation.handler;

import java.util.function.Consumer;

import com.google.common.base.Supplier;
import com.voxlr.marmoset.model.AuthUser;

public abstract class ValidationHandler<T> {
    
    public void validate(AuthUser authUser, Supplier<T> getter) {
	ValidationResult<T> result = new ValidationResult<>();
	validate(authUser, getter.get(), result);
    }
    
    public void validate(AuthUser authUser, Supplier<T> getter, Consumer<T> setter) {
	ValidationResult<T> result = new ValidationResult<>();
	validate(authUser, getter.get(), result);
	
	if (setter != null && result.resultSet) {
	    setter.accept(result.getResult());
	}
    }
    
    abstract void validate(AuthUser authUser, T input, ValidationResult<T> result);
}
