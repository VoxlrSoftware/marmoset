package com.voxlr.marmoset.auth;

import lombok.Getter;

@Getter
public enum Authority {
    MODIFY_ACCOUNT("modify_account", "Can modify associated account"),
    MODIFY_TEAM("modify_team", "Can modify associated team"),
    MODIFY_COMPANY("modify_company", "Can modify associated company"),
    VIEW_TEAM("view_team", "Can view team"),
    VIEW_COMPANY("view_company", "Can view company");
    
    private String id;
    private String description;
    
    Authority(String id, String description) {
	this.id = id;
	this.description = description;
    }
}
