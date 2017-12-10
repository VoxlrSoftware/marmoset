package com.voxlr.marmoset.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import com.voxlr.marmoset.service.TeamService;

public class TeamExistsValidator implements ConstraintValidator<TeamExistsConstraint, String> {
    @Autowired
    TeamService teamService;

    @Override
    public boolean isValid(String id, ConstraintValidatorContext context) {
	return id == null || teamService.validateExists(id);
    }
}
