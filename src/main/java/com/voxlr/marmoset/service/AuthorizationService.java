package com.voxlr.marmoset.service;

import com.voxlr.marmoset.auth.Authority;
import com.voxlr.marmoset.model.*;
import com.voxlr.marmoset.model.persistence.Company;
import com.voxlr.marmoset.model.persistence.User;
import com.voxlr.marmoset.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.voxlr.marmoset.util.CompareUtils.safeEquals;

@Service
public class AuthorizationService {

  @Autowired private UserRepository userRepository;

  boolean isSameUser(AuthUser authUser, GlobalEntity entity) {
    return safeEquals(authUser.getId(), entity.getId());
  }

  boolean isSameUser(AuthUser authUser, UserScopedEntity entity) {
    return safeEquals(authUser.getId(), entity.getUserId());
  }

  boolean isSameCompany(AuthUser authUser, UserScopedEntity entity) {
    User user = userRepository.getAssociationForUser(entity.getUserId());
    return user != null && safeEquals(authUser.getCompanyId(), user.getCompanyId());
  }

  boolean isSameCompany(AuthUser authUser, Company entity) {
    return safeEquals(authUser.getCompanyId(), entity.getId());
  }

  boolean isSameCompany(AuthUser authUser, CompanyScopedEntity entity) {
    return safeEquals(authUser.getCompanyId(), entity.getCompanyId());
  }

  boolean isSameTeam(AuthUser authUser, UserScopedEntity entity) {
    User user = userRepository.getAssociationForUser(entity.getUserId());
    return user != null && safeEquals(authUser.getTeamId(), user.getTeamId());
  }

  boolean isSameTeam(AuthUser authUser, TeamScopedEntity entity) {
    return safeEquals(authUser.getTeamId(), entity.getTeamId());
  }

  boolean typeInherits(Class<? extends GlobalEntity> type, Class<?> parentClass) {
    return parentClass.isAssignableFrom(type);
  }

  boolean typeIs(GlobalEntity entity, Class<?> entityClass) {
    return typeIs(entity.getClass(), entityClass);
  }

  boolean typeIs(Class<? extends GlobalEntity> type, Class<?> entityClass) {
    return type.equals(entityClass);
  }

  boolean hasAuthorities(AuthUser authUser, Authority... authorities) {
    return Arrays.stream(authorities).allMatch(authority -> authUser.hasCapability(authority));
  }

  Function<AuthUser, Boolean> isAdmin =
      authUser ->
          hasAuthorities(authUser, Authority.VIEW_ALL)
              || hasAuthorities(authUser, Authority.MODIFY_ALL);
  Function<AuthUser, Boolean> isSuperAdmin =
      authUser -> hasAuthorities(authUser, Authority.MODIFY_ALL);

  @SafeVarargs
  public static boolean firstValid(Supplier<Boolean>... actions) {
    return Arrays.stream(actions).anyMatch(action -> action.get());
  }

  private boolean canRead(AuthUser authUser, Company entity) {
    return firstValid(
        () -> hasAuthorities(authUser, Authority.VIEW_COMPANY) && isSameCompany(authUser, entity));
  }

  private boolean canRead(AuthUser authUser, UserScopedEntity entity) {
    return firstValid(
        () -> isSameUser(authUser, entity),
        () -> hasAuthorities(authUser, Authority.VIEW_TEAM) && isSameTeam(authUser, entity),
        () -> hasAuthorities(authUser, Authority.VIEW_COMPANY) && isSameCompany(authUser, entity));
  }

  private boolean canRead(AuthUser authUser, TeamScopedEntity entity) {
    return firstValid(
        () -> isSameUser(authUser, entity),
        () -> hasAuthorities(authUser, Authority.VIEW_TEAM) && isSameTeam(authUser, entity),
        () -> hasAuthorities(authUser, Authority.VIEW_COMPANY) && isSameCompany(authUser, entity));
  }

  private boolean canRead(AuthUser authUser, CompanyScopedEntity entity) {
    return firstValid(
        () -> isSameUser(authUser, entity),
        () -> hasAuthorities(authUser, Authority.VIEW_COMPANY) && isSameCompany(authUser, entity));
  }

  private boolean canWrite(AuthUser authUser, Company entity) {
    return firstValid(
        () ->
            hasAuthorities(authUser, Authority.MODIFY_COMPANY) && isSameCompany(authUser, entity));
  }

  private boolean canWrite(AuthUser authUser, UserScopedEntity entity) {
    return firstValid(
        () -> isSameUser(authUser, entity),
        () -> hasAuthorities(authUser, Authority.MODIFY_TEAM) && isSameTeam(authUser, entity),
        () ->
            hasAuthorities(authUser, Authority.MODIFY_COMPANY) && isSameCompany(authUser, entity));
  }

  private boolean canWrite(AuthUser authUser, TeamScopedEntity entity) {
    return firstValid(
        () -> isSameUser(authUser, entity),
        () -> hasAuthorities(authUser, Authority.MODIFY_TEAM) && isSameTeam(authUser, entity),
        () ->
            hasAuthorities(authUser, Authority.MODIFY_COMPANY) && isSameCompany(authUser, entity));
  }

  private boolean canWrite(AuthUser authUser, CompanyScopedEntity entity) {
    return firstValid(
        () -> isSameUser(authUser, entity),
        () ->
            hasAuthorities(authUser, Authority.MODIFY_COMPANY) && isSameCompany(authUser, entity));
  }

  public boolean canRead(AuthUser authUser, GlobalEntity entity) {
    return firstValid(
        () ->
            typeInherits(entity.getClass(), UserScopedEntity.class)
                && canRead(authUser, (UserScopedEntity) entity),
        () ->
            typeInherits(entity.getClass(), TeamScopedEntity.class)
                && canRead(authUser, (TeamScopedEntity) entity),
        () ->
            typeInherits(entity.getClass(), CompanyScopedEntity.class)
                && canRead(authUser, (CompanyScopedEntity) entity),
        () -> typeIs(entity, Company.class) && canRead(authUser, (Company) entity),
        () -> isAdmin.apply(authUser));
  }

  public boolean canWrite(AuthUser authUser, GlobalEntity entity) {
    return firstValid(
        () ->
            typeInherits(entity.getClass(), UserScopedEntity.class)
                && canWrite(authUser, (UserScopedEntity) entity),
        () ->
            typeInherits(entity.getClass(), TeamScopedEntity.class)
                && canWrite(authUser, (TeamScopedEntity) entity),
        () ->
            typeInherits(entity.getClass(), CompanyScopedEntity.class)
                && canWrite(authUser, (CompanyScopedEntity) entity),
        () -> typeIs(entity, Company.class) && canWrite(authUser, (Company) entity),
        () -> isSuperAdmin.apply(authUser));
  }

  public boolean canCreate(AuthUser authUser, Class<? extends GlobalEntity> toCreate) {
    return firstValid(
        () ->
            typeInherits(toCreate, UserScopedEntity.class)
                && hasAuthorities(authUser, Authority.MODIFY_ACCOUNT),
        () ->
            typeInherits(toCreate, TeamScopedEntity.class)
                && hasAuthorities(authUser, Authority.MODIFY_TEAM),
        () ->
            typeInherits(toCreate, CompanyScopedEntity.class)
                && hasAuthorities(authUser, Authority.MODIFY_COMPANY),
        () -> isSuperAdmin.apply(authUser));
  }
}
