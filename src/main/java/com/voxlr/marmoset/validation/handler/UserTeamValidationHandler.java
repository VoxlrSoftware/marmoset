package com.voxlr.marmoset.validation.handler;

import com.voxlr.marmoset.auth.Authority;
import com.voxlr.marmoset.model.AuthUser;

public class UserTeamValidationHandler extends ValidationHandler<String> {

  @Override
  void validate(AuthUser authUser, String input, ValidationResult<String> result) {
    if (!authUser.hasCapability(Authority.MODIFY_COMPANY) || input == null) {
      result.setResult(authUser.getTeamId());
    }
  }
}
