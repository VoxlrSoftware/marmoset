package com.voxlr.marmoset.validation.constraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.voxlr.marmoset.model.PhoneNumberHolder;

public class PhoneNumberValidator implements ConstraintValidator<PhoneNumberValidConstraint, PhoneNumberHolder>{

    @Override
    public boolean isValid(PhoneNumberHolder value, ConstraintValidatorContext context) {
	if (value == null) {
	    return true;
	}
	
	try {
	    PhoneNumber phoneNumber = PhoneNumberUtil.getInstance().parse(value.getNumber(), "US");
	    phoneNumber.setExtension(value.getExtension());
	    return PhoneNumberUtil.getInstance().isPossibleNumber(phoneNumber);
	} catch (Exception e) {
	    return false;
	}
    }

}
