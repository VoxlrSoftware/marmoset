package com.voxlr.marmoset.auth;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import lombok.Getter;

@Getter
public enum Authority {
    MODIFY_ACCOUNT("modify_account", "Can modify associated account"),
    MODIFY_TEAM("modify_team", "Can modify associated team"),
    MODIFY_COMPANY("modify_company", "Can modify associated company"),
    MODIFY_ALL("modify_all", "Can modify pretty much anything"),
    VIEW_TEAM("view_team", "Can view team"),
    VIEW_COMPANY("view_company", "Can view company"),
    VIEW_ALL("view_all", "Can view pretty much anything");
    
    private String id;
    private String description;
    
    Authority(String id, String description) {
	this.id = id;
	this.description = description;
    }
    
    private static Map<Authority, Set<Authority>> capabilities;
    private static final Map<String, Authority> authorityMap;
    
    static {
	authorityMap = new HashMap<String, Authority>();
	Arrays.stream(Authority.values()).forEach(authority -> {
	    authorityMap.put(authority.getId(), authority);
	});
	
	capabilities = new HashMap<Authority, Set<Authority>>();
	capabilities.put(MODIFY_ACCOUNT, new HashSet<Authority>(Arrays.asList(MODIFY_ACCOUNT, MODIFY_TEAM, MODIFY_COMPANY, MODIFY_ALL)));
	capabilities.put(MODIFY_TEAM, new HashSet<Authority>(Arrays.asList(MODIFY_TEAM, MODIFY_COMPANY, MODIFY_ALL)));
	capabilities.put(MODIFY_COMPANY, new HashSet<Authority>(Arrays.asList(MODIFY_COMPANY, MODIFY_ALL)));
	capabilities.put(MODIFY_ALL, new HashSet<Authority>(Arrays.asList(MODIFY_ALL)));

	capabilities.put(VIEW_TEAM, new HashSet<Authority>(Arrays.asList(VIEW_TEAM, MODIFY_TEAM, VIEW_COMPANY, MODIFY_COMPANY, VIEW_ALL, MODIFY_ALL)));
	capabilities.put(VIEW_COMPANY, new HashSet<Authority>(Arrays.asList(VIEW_COMPANY, VIEW_ALL, MODIFY_ALL)));
	capabilities.put(VIEW_ALL, new HashSet<Authority>(Arrays.asList(VIEW_ALL, MODIFY_ALL)));
    }
    
    public static Authority get(String authority) {
	return authorityMap.get(authority);
    }
    
    public static boolean hasCapability(Authority authority, Set<Authority> authorities) {
	Set<Authority> capabilityList = capabilities.get(authority);
	Set<Authority> currentAuthorities = new HashSet<>(authorities);
	currentAuthorities.retainAll(capabilityList);
	return currentAuthorities.size() > 0;
    }
    
    public static boolean hasCapability(String authority, Set<Authority> authorities) {
	return hasCapability(get(authority), authorities);
    }
}
