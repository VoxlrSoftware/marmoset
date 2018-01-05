package com.voxlr.marmoset.validation.validator;

import com.voxlr.marmoset.model.AuthUser;
import com.voxlr.marmoset.model.persistence.dto.UserUpdateDTO;
import com.voxlr.marmoset.validation.handler.UserRoleValidationHandler;

@TypeValidator(forClass = UserUpdateDTO.class)
public class UserUpdateValidator implements Validator<UserUpdateDTO> {
    
    private final UserRoleValidationHandler userRoleValidationHandler
    	= new UserRoleValidationHandler();

    @Override
    public void validate(AuthUser authUser, UserUpdateDTO entity) {
	userRoleValidationHandler.validate(
		authUser, entity::getRole);
    }

}
