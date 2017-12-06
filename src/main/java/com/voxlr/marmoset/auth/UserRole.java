package com.voxlr.marmoset.auth;

import lombok.Getter;

@Getter
public enum UserRole {
    COMPANY_ADMIN(
	    "CompanyAdmin",
	    "View and write access to companies",
	    Authority.MODIFY_COMPANY,
	    Authority.VIEW_COMPANY,
	    Authority.CREATE_USER,
	    Authority.DELETE_USER),
    
    COMPANY_READONLY(
	    "CompanyReadonly",
	    "View access to companies",
	    Authority.VIEW_COMPANY),
    
    TEAM_ADMIN(
	    "TeamAdmin",
	    "View and write access to teams",
	    Authority.MODIFY_TEAM,
	    Authority.VIEW_TEAM,
	    Authority.CREATE_USER,
	    Authority.DELETE_USER),
    
    TEAM_READONLY(
	    "TeamReadonly",
	    "View access to teams",
	    Authority.VIEW_TEAM),
    
    MEMBER(
	    "Member",
	    "View and write access to account",
	    Authority.MODIFY_ACCOUNT);

    private final String id;
    private final String description;
    private final Authority[] authorities;

    UserRole(String id, String description, Authority... authorities) {
        this.id = id;
        this.description = description;
        this.authorities = authorities;
    }
    
    public static String get(UserRole role) {
	return role.getId();
    }
}