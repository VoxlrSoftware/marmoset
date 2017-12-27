package com.voxlr.marmoset.validation.constraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

public class PhoneNumberValidator implements ConstraintValidator<PhoneNumberValidConstraint, String>{

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
	PhoneNumber phoneNumber = new PhoneNumber();
	phoneNumber.setRawInput(value);
	return PhoneNumberUtil.getInstance().isValidNumber(phoneNumber);
    }

}
