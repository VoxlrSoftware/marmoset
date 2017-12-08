package com.voxlr.marmoset.service;

import java.util.stream.Stream;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import com.voxlr.marmoset.auth.Authority;
import com.voxlr.marmoset.model.AuthUser;
import com.voxlr.marmoset.model.Entity;
import com.voxlr.marmoset.model.TeamScopedEntity;

@Service
public class AuthorizationService {

    boolean isSameUser(AuthUser authUser, Entity entity) {
	return authUser.getId() == entity.getId();
    }
    
    boolean hasOneOrMoreAuthorities(AuthUser authUser, Authority... authorities) {
	Stream<GrantedAuthority> grantedAuths = authUser.getAuthorities().stream();
	
//	return Arrays.stream(authorities).allMatch(authority -> {
//	   grantedAuths.anyMatch(grantedAuthority -> {
//	      grantedAuthority.getAuthority().equals(authority);
//	   });
//	});
	return true;
    }
    
    public boolean authorizeRead(
	    AuthUser authUser,
	    TeamScopedEntity entity) {
	
	if (isSameUser(authUser, entity)) {
	    return true;
	}
	
	return true;
    }
}
