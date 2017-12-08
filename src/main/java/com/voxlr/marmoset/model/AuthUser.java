package com.voxlr.marmoset.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.voxlr.marmoset.model.persistence.User;

public class AuthUser extends org.springframework.security.core.userdetails.User {

    private String id;
    private String teamId;
    private String companyId;
    
    public AuthUser(String username, String password, Collection<? extends GrantedAuthority> authorities) {
	super(username, password, authorities);
    }
    
    public AuthUser() {
	super("", "", new ArrayList<>());
    }
    
    public static AuthUser buildFromUser(User user) {
	List<GrantedAuthority> authorities = new ArrayList<>();
        user.getRoles()
        	.stream()
        	.flatMap(x -> Arrays.stream(x.getAuthorities()))
        	.distinct()
        	.forEach(authority ->
        		authorities.add(new SimpleGrantedAuthority(authority.getId()))
        	);
        
        AuthUser authUser = new AuthUser(user.getEmail(), user.getPassword(), authorities);
        authUser.setId(user.getId());
        authUser.setCompanyId(user.getCompanyId());
        authUser.setTeamId(user.getTeamId());
        
        return authUser;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }


}
