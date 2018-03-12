package com.voxlr.marmoset.auth;

import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Getter
public enum UserRole {
  COMPANY_ADMIN(
      "CompanyAdmin",
      "View and write access to companies",
      500,
      Authority.MODIFY_COMPANY,
      Authority.VIEW_COMPANY),

  COMPANY_READONLY("CompanyReadonly", "View access to companies", 400, Authority.VIEW_COMPANY),

  TEAM_ADMIN(
      "TeamAdmin",
      "View and write access to teams",
      300,
      Authority.MODIFY_TEAM,
      Authority.VIEW_TEAM),

  TEAM_READONLY("TeamReadonly", "View access to teams", 200, Authority.VIEW_TEAM),

  MEMBER("Member", "View and write access to account", 100, Authority.MODIFY_ACCOUNT),
  ADMIN("Admin", "View access across companies", 1000, Authority.VIEW_ALL),
  SUPER_ADMIN(
      "SuperAdmin",
      "View and write access across companies",
      2000,
      Authority.MODIFY_ALL,
      Authority.VIEW_ALL);

  private final String id;
  private final String description;
  private final int level;
  private final Authority[] authorities;

  UserRole(String id, String description, int level, Authority... authorities) {
    this.id = id;
    this.description = description;
    this.level = level;
    this.authorities = authorities;
  }

  private static final Map<String, UserRole> roleMap = new HashMap<String, UserRole>();

  static {
    Arrays.stream(UserRole.values()).forEach(x -> roleMap.put(x.id, x));
  }

  public static String get(UserRole role) {
    return role.getId();
  }

  public static UserRole get(String role) {
    return roleMap.get(role);
  }

  public static boolean isOrExceeds(UserRole role, UserRole userRole) {
    return userRole.getLevel() >= role.getLevel();
  }
}
