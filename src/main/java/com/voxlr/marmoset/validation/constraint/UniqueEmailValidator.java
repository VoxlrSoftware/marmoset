package com.voxlr.marmoset.validation.constraint;

import com.voxlr.marmoset.service.domain.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UniqueEmailValidator implements ConstraintValidator<UniqueEmailConstraint, String> {
  @Autowired UserService userService;

  @Override
  public boolean isValid(String email, ConstraintValidatorContext context) {
    return userService.validateUniqueEmail(email);
  }
}
