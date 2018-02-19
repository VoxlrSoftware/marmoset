package com.voxlr.marmoset.validation.validator;

import com.voxlr.marmoset.model.AuthUser;
import com.voxlr.marmoset.model.persistence.dto.UserCreateDTO;
import com.voxlr.marmoset.validation.handler.UserCompanyValidationHandler;
import com.voxlr.marmoset.validation.handler.UserRoleValidationHandler;
import com.voxlr.marmoset.validation.handler.UserTeamValidationHandler;

@TypeValidator(forClass = UserCreateDTO.class)
public class UserCreateValidator implements Validator<UserCreateDTO> {
    
    private final UserCompanyValidationHandler userCompanyValidationHandler
    	= new UserCompanyValidationHandler();
    private final UserTeamValidationHandler userTeamValidationHandler
    	= new UserTeamValidationHandler();
    private final UserRoleValidationHandler userRoleValidationHandler
    	= new UserRoleValidationHandler();

    @Override
    public void validate(AuthUser authUser, UserCreateDTO entity) throws Exception {
	userCompanyValidationHandler.validate(
		authUser, entity::getCompanyId, entity::setCompanyId);
	userTeamValidationHandler.validate(
		authUser, entity::getTeamId, entity::setTeamId);
	userRoleValidationHandler.validate(
		authUser, entity::getRole);
    }

}
