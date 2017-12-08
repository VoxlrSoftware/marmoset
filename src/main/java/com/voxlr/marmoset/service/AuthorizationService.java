package com.voxlr.marmoset.service;

import java.util.Arrays;
import java.util.function.Supplier;

import org.springframework.stereotype.Service;

import com.voxlr.marmoset.auth.Authority;
import com.voxlr.marmoset.model.AuthUser;
import com.voxlr.marmoset.model.CompanyScopedEntity;
import com.voxlr.marmoset.model.Entity;
import com.voxlr.marmoset.model.TeamScopedEntity;

@Service
public class AuthorizationService {

    boolean isSameUser(AuthUser authUser, Entity entity) {
	return authUser.getId().equals(entity.getId());
    }
    
    boolean isSameCompany(AuthUser authUser, CompanyScopedEntity entity) {
	return authUser.getCompanyId().equals(entity.getCompanyId());
    }
    
    boolean isSameTeam(AuthUser authUser, TeamScopedEntity entity) {
	return authUser.getTeamId().equals(entity.getTeamId());
    }
    
    boolean hasAuthorities(AuthUser authUser, Authority... authorities) {
	return Arrays.stream(authorities).allMatch(authority -> 
	   authUser.getAuthoritySet().contains(authority.getId())
	);
    }
    
    @SafeVarargs
    public static boolean firstValid(Supplier<Boolean>... actions) {
	return Arrays.stream(actions).anyMatch(action ->
		action.get()
	);
    }
    
    public boolean authorizeRead(
	    AuthUser authUser,
	    TeamScopedEntity entity) {
	return firstValid(
		() -> isSameUser(authUser, entity),
		() -> hasAuthorities(authUser, Authority.VIEW_TEAM) && 
			isSameTeam(authUser, entity),
		() -> hasAuthorities(authUser, Authority.VIEW_COMPANY) &&
			isSameCompany(authUser, entity)
		);
    }
    
    public boolean authorizeWrite(
	    AuthUser authUser,
	    TeamScopedEntity entity) {
	return firstValid(
		() -> isSameUser(authUser, entity),
		() -> hasAuthorities(authUser, Authority.MODIFY_TEAM) && 
			isSameTeam(authUser, entity),
		() -> hasAuthorities(authUser, Authority.MODIFY_COMPANY) &&
			isSameCompany(authUser, entity)
		);
    }
}
