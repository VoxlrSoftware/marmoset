package com.voxlr.marmoset.convert;

import lombok.Getter;

@Getter
public class ConvertFailedResult<T, U> extends ConvertResult<T> {
    private U source;
    
    public ConvertFailedResult(U source) {
	super(null);
	this.source = source;
    }
}
