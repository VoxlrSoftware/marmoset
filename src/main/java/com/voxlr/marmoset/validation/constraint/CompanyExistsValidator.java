package com.voxlr.marmoset.validation.constraint;

import com.voxlr.marmoset.service.domain.CompanyService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CompanyExistsValidator
    implements ConstraintValidator<CompanyExistsConstraint, ObjectId> {
  @Autowired CompanyService companyService;

  @Override
  public boolean isValid(ObjectId id, ConstraintValidatorContext context) {
    return id == null || companyService.validateExists(id);
  }
}
