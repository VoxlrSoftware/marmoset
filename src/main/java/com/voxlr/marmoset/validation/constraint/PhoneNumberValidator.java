package com.voxlr.marmoset.validation.constraint;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.voxlr.marmoset.model.PhoneNumberHolder;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PhoneNumberValidator
    implements ConstraintValidator<PhoneNumberValidConstraint, PhoneNumberHolder> {

  private boolean isRequired;

  @Override
  public void initialize(PhoneNumberValidConstraint constraintAnnotation) {
    isRequired = constraintAnnotation.required();
    ConstraintValidator.super.initialize(constraintAnnotation);
  }

  @Override
  public boolean isValid(PhoneNumberHolder value, ConstraintValidatorContext context) {
    if (value == null) {
      return !isRequired;
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
