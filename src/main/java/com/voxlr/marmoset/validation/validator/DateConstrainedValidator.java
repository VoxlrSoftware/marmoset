package com.voxlr.marmoset.validation.validator;

import java.util.Date;

import com.voxlr.marmoset.model.AuthUser;
import com.voxlr.marmoset.model.dto.DateConstrained;
import com.voxlr.marmoset.util.exception.InvalidArgumentsException;

@TypeValidator(forClass = DateConstrained.class)
public class DateConstrainedValidator implements Validator<DateConstrained> {

    @Override
    public void validate(AuthUser authUser, DateConstrained entity) throws Exception {
	Date today = new Date();
	
	if (entity.getStartDate() != null) {
	    if (entity.getEndDate() != null && entity.getStartDate().after(entity.getEndDate())) {
		throw new InvalidArgumentsException("startDate must be before endDate");
	    }
	    
	    if (entity.getStartDate().after(today)) {
		entity.setStartDate(today);
	    }
	}
	
	if (entity.getEndDate() != null) {
	    if (entity.getEndDate().after(today)) {
		entity.setEndDate(today);
	    }
	}
    }
}
