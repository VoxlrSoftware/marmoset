package com.voxlr.marmoset.validation.handler;

import java.util.function.Consumer;

import com.google.common.base.Supplier;
import com.voxlr.marmoset.auth.Authority;
import com.voxlr.marmoset.model.AuthUser;

public class UserCompanyValidationHandler implements ValidationHandler<String> {

    public void validate(AuthUser authUser, Supplier<String> getter, Consumer<String> setter) {
	String companyId = getter.get();
	
	if (!authUser.hasCapability(Authority.MODIFY_ALL) || companyId == null) {
	    setter.accept(authUser.getCompanyId());
	}
    }

}
