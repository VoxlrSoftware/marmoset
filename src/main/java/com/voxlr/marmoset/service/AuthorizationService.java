package com.voxlr.marmoset.service;

import java.util.Arrays;
import java.util.function.Function;
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
    
    boolean typeInherits(Class<? extends Entity> type, Class parentClass) {
	return parentClass.isAssignableFrom(type);
    }
    
    boolean hasAuthorities(AuthUser authUser, Authority... authorities) {
	return Arrays.stream(authorities).allMatch(authority -> 
		authUser.hasAuthority(authority)
	);
    }
    
    Function<AuthUser, Boolean> isAdmin = authUser -> hasAuthorities(authUser, Authority.VIEW_ALL);
    Function<AuthUser, Boolean> isSuperAdmin = authUser -> hasAuthorities(authUser, Authority.MODIFY_ALL);
    
    @SafeVarargs
    public static boolean firstValid(Supplier<Boolean>... actions) {
	return Arrays.stream(actions).anyMatch(action ->
		action.get()
	);
    }
    
    boolean canRead(AuthUser authUser, TeamScopedEntity entity) {
	return firstValid(
		() -> isAdmin.apply(authUser),
		() -> isSameUser(authUser, entity),
		() -> hasAuthorities(authUser, Authority.VIEW_TEAM) && 
			isSameTeam(authUser, entity),
		() -> hasAuthorities(authUser, Authority.VIEW_COMPANY) &&
			isSameCompany(authUser, entity)
		);
    }
    
    boolean canRead(AuthUser authUser, CompanyScopedEntity entity) {
	return firstValid(
		() -> isAdmin.apply(authUser),
		() -> isSameUser(authUser, entity),
		() -> hasAuthorities(authUser, Authority.VIEW_COMPANY) &&
			isSameCompany(authUser, entity)
		);
    }

    boolean canWrite(AuthUser authUser, TeamScopedEntity entity) {
	return firstValid(
		() -> isSuperAdmin.apply(authUser),
		() -> isSameUser(authUser, entity),
		() -> hasAuthorities(authUser, Authority.MODIFY_TEAM) && 
			isSameTeam(authUser, entity),
		() -> hasAuthorities(authUser, Authority.MODIFY_COMPANY) &&
			isSameCompany(authUser, entity)
		);
    }
    
    boolean canWrite(AuthUser authUser, CompanyScopedEntity entity) {
   	return firstValid(
   		() -> isSuperAdmin.apply(authUser),
   		() -> isSameUser(authUser, entity),
   		() -> hasAuthorities(authUser, Authority.MODIFY_COMPANY) &&
   			isSameCompany(authUser, entity)
   		);
       }
    
    public boolean canRead(AuthUser authUser, Entity entity) {
	return firstValid(
		() -> typeInherits(entity.getClass(), TeamScopedEntity.class) && canRead(authUser, (TeamScopedEntity) entity),
		() -> typeInherits(entity.getClass(), CompanyScopedEntity.class) && canRead(authUser, (CompanyScopedEntity) entity),
		() -> isAdmin.apply(authUser)
		);
    }
    
    public boolean canWrite(AuthUser authUser, Entity entity) {
	return firstValid(
		() -> typeInherits(entity.getClass(), TeamScopedEntity.class) && canWrite(authUser, (TeamScopedEntity) entity),
		() -> typeInherits(entity.getClass(), CompanyScopedEntity.class) && canWrite(authUser, (CompanyScopedEntity) entity),
		() -> isSuperAdmin.apply(authUser)
		);
    }
    
    public boolean canCreate(AuthUser authUser, Class<? extends Entity> toCreate) {
	return firstValid(
		() -> isSuperAdmin.apply(authUser),
		() -> typeInherits(toCreate, TeamScopedEntity.class) &&
			hasAuthorities(authUser, Authority.MODIFY_TEAM),
			() -> typeInherits(toCreate, CompanyScopedEntity.class) &&
			hasAuthorities(authUser, Authority.MODIFY_COMPANY)
		);
    }
}
