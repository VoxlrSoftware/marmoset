package com.voxlr.marmoset.validation.constraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import com.voxlr.marmoset.service.domain.CompanyService;

public class CompanyExistsValidator implements ConstraintValidator<CompanyExistsConstraint, String> {
    @Autowired
    CompanyService companyService;

    @Override
    public boolean isValid(String id, ConstraintValidatorContext context) {
	return id == null || companyService.validateExists(id);
    }
}
