package com.voxlr.marmoset.validation.handler;

import com.voxlr.marmoset.auth.Authority;
import com.voxlr.marmoset.model.AuthUser;
import org.bson.types.ObjectId;

public class UserCompanyValidationHandler extends ValidationHandler<ObjectId> {

  @Override
  void validate(AuthUser authUser, ObjectId input, ValidationResult<ObjectId> result) {
    if (!authUser.hasCapability(Authority.MODIFY_ALL) || input == null) {
      result.setResult(authUser.getCompanyId());
    }
  }
}
