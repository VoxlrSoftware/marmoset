package com.voxlr.marmoset.validation.validator;

import org.joda.time.DateTime;

import com.voxlr.marmoset.exception.InvalidArgumentsException;
import com.voxlr.marmoset.model.AuthUser;
import com.voxlr.marmoset.model.dto.DateConstrained;

@TypeValidator(forClass = DateConstrained.class)
public class DateConstrainedValidator implements Validator<DateConstrained> {

    @Override
    public void validate(AuthUser authUser, DateConstrained entity) throws Exception {
	DateTime today = new DateTime();
	
	if (entity.getStartDate() != null) {
	    if (entity.getEndDate() != null && entity.getStartDate().isAfter(entity.getEndDate())) {
		throw new InvalidArgumentsException("startDate must be before endDate");
	    }
	    
	    if (entity.getStartDate().isAfter(today)) {
		entity.setStartDate(today);
	    }
	}
	
	if (entity.getEndDate() != null) {
	    if (entity.getEndDate().isAfter(today)) {
		entity.setEndDate(today);
	    }
	}
    }
}
