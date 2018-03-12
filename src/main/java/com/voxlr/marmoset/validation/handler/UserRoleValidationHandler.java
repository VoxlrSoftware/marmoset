package com.voxlr.marmoset.validation.handler;

import com.voxlr.marmoset.auth.UserRole;
import com.voxlr.marmoset.model.AuthUser;
import lombok.NoArgsConstructor;
import org.springframework.security.oauth2.common.exceptions.UnauthorizedUserException;

@NoArgsConstructor
public class UserRoleValidationHandler extends ValidationHandler<String> {

  private String action = "create";

  public UserRoleValidationHandler(String action) {
    super();
    this.action = action;
  }

  @Override
  void validate(AuthUser authUser, String input, ValidationResult<String> result) {
    if (input != null) {
      UserRole desiredRole = UserRole.get(input);
      if (!authUser.isRoleAbove(desiredRole)) {
        throw new UnauthorizedUserException(
            "Account unauthorized to " + action + " user with role [" + desiredRole.getId() + "].");
      }
    }
  }
}
