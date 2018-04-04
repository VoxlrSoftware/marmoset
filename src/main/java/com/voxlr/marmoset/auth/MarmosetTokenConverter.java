package com.voxlr.marmoset.auth;

import com.voxlr.marmoset.model.AuthUser;
import org.bson.types.ObjectId;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import java.util.Map;

public class MarmosetTokenConverter extends DefaultUserAuthenticationConverter {

  private final JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();

  @Override
  public Authentication extractAuthentication(Map<String, ?> map) {
    OAuth2Authentication authentication = jwtAccessTokenConverter.extractAuthentication(map);

    AuthUser user =
        new AuthUser(
            authentication.getPrincipal().toString(),
            authentication.getCredentials().toString(),
            authentication.getAuthorities());
    user.setCompanyId(new ObjectId(map.get("companyId").toString()));
    user.setTeamId(new ObjectId(map.get("teamId").toString()));
    user.setId(new ObjectId(map.get("id").toString()));
    user.setRoleString(map.get("role").toString());

    return new UsernamePasswordAuthenticationToken(
        user, authentication.getCredentials(), authentication.getAuthorities());
  }
}
