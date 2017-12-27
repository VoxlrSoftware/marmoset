package com.voxlr.marmoset.validation.constraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import com.voxlr.marmoset.service.UserService;

public class UniqueEmailValidator implements ConstraintValidator<UniqueEmailConstraint, String> {
    @Autowired
    UserService userService;

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
	return userService.validateUniqueEmail(email);
    }
}
