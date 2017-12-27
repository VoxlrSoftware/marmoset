package com.voxlr.marmoset.validation.handler;

import lombok.Getter;
import lombok.Setter;
import lombok.AccessLevel;

@Getter
@Setter
public class ValidationResult<T> {
    T result;
    
    @Setter(AccessLevel.NONE)
    boolean resultSet = false;
    
    public void setResult(T result) {
	this.result = result;
	this.resultSet = true;
    }
}
