package com.voxlr.marmoset.validation.handler;

import com.voxlr.marmoset.auth.Authority;
import com.voxlr.marmoset.model.AuthUser;

public class UserCompanyValidationHandler extends ValidationHandler<String> {

  @Override
  void validate(AuthUser authUser, String input, ValidationResult<String> result) {
    if (!authUser.hasCapability(Authority.MODIFY_ALL) || input == null) {
      result.setResult(authUser.getCompanyId());
    }
  }
}
