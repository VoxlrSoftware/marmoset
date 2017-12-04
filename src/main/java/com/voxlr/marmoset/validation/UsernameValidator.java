package com.voxlr.marmoset.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import com.voxlr.marmoset.repositories.UserRepository;

public class UsernameValidator implements ConstraintValidator<UsernameConstraint, String> {
    @Autowired
    UserRepository userRepository;
    
    @Override
    public void initialize(UsernameConstraint username) {
    }

    @Override
    public boolean isValid(String username, ConstraintValidatorContext context) {
	return userRepository.validateUsername(username) == null;
    }
}
