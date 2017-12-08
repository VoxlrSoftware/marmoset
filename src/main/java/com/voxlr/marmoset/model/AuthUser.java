package com.voxlr.marmoset.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.voxlr.marmoset.model.persistence.User;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthUser extends org.springframework.security.core.userdetails.User implements TeamScopedEntity {

    private String id;
    private String teamId;
    private String companyId;
    private Set<String> authoritySet;
    
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
		.map(authority -> authority.getAuthority())
		.collect(Collectors.toSet()));
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
}
