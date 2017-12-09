package com.voxlr.marmoset.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.voxlr.marmoset.auth.Authority;
import com.voxlr.marmoset.auth.UserRole;
import com.voxlr.marmoset.model.persistence.User;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthUser extends org.springframework.security.core.userdetails.User implements TeamScopedEntity {

    private String id;
    private String teamId;
    private String companyId;
    private UserRole role;
    private Set<Authority> authoritySet;
    
    public AuthUser(String username, String password, Collection<? extends GrantedAuthority> authorities) {
	super(username, password, authorities);
	updateAuthoritySet();
    }
    
    public AuthUser() {
	super("", "", new ArrayList<>());
    }
    
    void updateAuthoritySet() {
	this.setAuthoritySet(this.getAuthorities()
		.stream()
		.map(authority -> Authority.get(authority.getAuthority()))
		.collect(Collectors.toSet()));
    }
    
    public boolean hasAuthority(Authority authority) {
	return authoritySet.contains(authority);
    }
    
    public boolean hasCapability(Authority authority) {
	return Authority.hasCapability(authority, authoritySet);
    }
    
    public boolean isRoleAbove(UserRole role) {
	return UserRole.isOrExceeds(role, this.getRole());
    }
    
    public static AuthUser buildFromUser(User user) {
	List<GrantedAuthority> authorities = new ArrayList<>();
        Arrays.stream(user.getRole().getAuthorities())
        	.forEach(authority ->
        		authorities.add(new SimpleGrantedAuthority(authority.getId()))
        	);
        
        AuthUser authUser = new AuthUser(user.getEmail(), user.getPassword(), authorities);
        authUser.setId(user.getId());
        authUser.setCompanyId(user.getCompanyId());
        authUser.setTeamId(user.getTeamId());
        authUser.setRole(user.getRole());
        
        return authUser;
    }
    
    public void setRoleString(String role) {
	UserRole userRole = UserRole.get(role);
	if (userRole != null) {
	    setRole(userRole);
	} else {
	    setRole(UserRole.MEMBER);
	}
    }
}
