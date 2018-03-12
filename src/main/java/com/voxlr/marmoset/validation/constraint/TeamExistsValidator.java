package com.voxlr.marmoset.validation.constraint;

import com.voxlr.marmoset.service.domain.TeamService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class TeamExistsValidator implements ConstraintValidator<TeamExistsConstraint, String> {
  @Autowired TeamService teamService;

  @Override
  public boolean isValid(String id, ConstraintValidatorContext context) {
    return id == null || teamService.validateExists(id);
  }
}
