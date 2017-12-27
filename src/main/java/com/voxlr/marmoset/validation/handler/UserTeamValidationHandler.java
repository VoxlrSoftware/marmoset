package com.voxlr.marmoset.validation.handler;

import java.util.function.Consumer;

import com.google.common.base.Supplier;
import com.voxlr.marmoset.auth.Authority;
import com.voxlr.marmoset.model.AuthUser;

public class UserTeamValidationHandler implements ValidationHandler<String> {

    public void validate(AuthUser authUser, Supplier<String> getter, Consumer<String> setter) {
	String teamId = getter.get();
	
	if (!authUser.hasCapability(Authority.MODIFY_COMPANY) || teamId == null) {
	    setter.accept(authUser.getTeamId());
	}
    }

}
