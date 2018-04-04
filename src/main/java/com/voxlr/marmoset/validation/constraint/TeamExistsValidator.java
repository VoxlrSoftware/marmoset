package com.voxlr.marmoset.validation.constraint;

import com.voxlr.marmoset.service.domain.TeamService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class TeamExistsValidator implements ConstraintValidator<TeamExistsConstraint, ObjectId> {
  @Autowired TeamService teamService;

  @Override
  public boolean isValid(ObjectId id, ConstraintValidatorContext context) {
    return id == null || teamService.validateExists(id);
  }
}
