package com.voxlr.marmoset.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import com.voxlr.marmoset.repositories.UserRepository;
import com.voxlr.marmoset.service.UserService;

public class UsernameValidator implements ConstraintValidator<UsernameConstraint, String> {
    @Autowired
    UserService userService;
    
    @Override
    public void initialize(UsernameConstraint username) {
    }

    @Override
    public boolean isValid(String username, ConstraintValidatorContext context) {
	return userService.validateUsername(username);
    }
}
