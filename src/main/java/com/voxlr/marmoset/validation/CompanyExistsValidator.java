package com.voxlr.marmoset.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import com.voxlr.marmoset.service.CompanyService;

public class CompanyExistsValidator implements ConstraintValidator<CompanyExistsConstraint, String> {
    @Autowired
    CompanyService companyService;

    @Override
    public boolean isValid(String id, ConstraintValidatorContext context) {
	return id == null || companyService.validateExists(id);
    }
}
