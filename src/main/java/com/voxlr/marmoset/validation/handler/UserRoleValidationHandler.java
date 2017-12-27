package com.voxlr.marmoset.validation.handler;

import java.util.function.Consumer;

import org.springframework.security.oauth2.common.exceptions.UnauthorizedUserException;

import com.google.common.base.Supplier;
import com.voxlr.marmoset.auth.UserRole;
import com.voxlr.marmoset.model.AuthUser;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UserRoleValidationHandler implements ValidationHandler<String> {
    
    private String action = "create";
    
    public UserRoleValidationHandler(String action) {
	this.action = action;
    }

    public void validate(AuthUser authUser, Supplier<String> getter, Consumer<String> setter) {
	String userRoleString = getter.get();
	
	if (userRoleString != null) {
	    UserRole desiredRole = UserRole.get(userRoleString);
	    if (!authUser.isRoleAbove(desiredRole)) {
		throw new UnauthorizedUserException("Account unauthorized to " + action + " user with role [" + desiredRole.getId() + "].");
	    }
	}
    }

}
