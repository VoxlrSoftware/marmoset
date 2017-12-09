package com.voxlr.marmoset.auth;

import java.util.Map;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import com.voxlr.marmoset.model.AuthUser;

public class MarmosetTokenConverter extends DefaultUserAuthenticationConverter {
    
    private final JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
    
    @Override
    public Authentication extractAuthentication(Map<String, ?> map) {
	OAuth2Authentication authentication = jwtAccessTokenConverter.extractAuthentication(map);
	
	AuthUser user = new AuthUser(authentication.getPrincipal().toString(),
		authentication.getCredentials().toString(),
		authentication.getAuthorities());
	user.setCompanyId(map.get("companyId").toString());
	user.setTeamId(map.get("teamId").toString());
	user.setId(map.get("id").toString());
	user.setRoleString(map.get("role").toString());
	
	return new UsernamePasswordAuthenticationToken(
		user,
		authentication.getCredentials(),
		authentication.getAuthorities());
    }
}
