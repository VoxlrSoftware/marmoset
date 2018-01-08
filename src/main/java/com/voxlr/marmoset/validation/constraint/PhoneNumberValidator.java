package com.voxlr.marmoset.validation.constraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

public class PhoneNumberValidator implements ConstraintValidator<PhoneNumberValidConstraint, String>{

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
	if (value == null) {
	    return true;
	}
	
	try {
	    PhoneNumber phoneNumber = PhoneNumberUtil.getInstance().parse(value, "US");
	    return PhoneNumberUtil.getInstance().isPossibleNumber(phoneNumber);
	} catch (Exception e) {
	    return false;
	}
    }

}
