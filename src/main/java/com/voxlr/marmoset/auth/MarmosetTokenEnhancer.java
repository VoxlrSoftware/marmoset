package com.voxlr.marmoset.auth;

import com.voxlr.marmoset.model.AuthUser;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import java.util.LinkedHashMap;
import java.util.Map;

public class MarmosetTokenEnhancer extends JwtAccessTokenConverter {

  @Override
  public OAuth2AccessToken enhance(
      OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
    AuthUser user = (AuthUser) authentication.getPrincipal();
    Map<String, Object> info =
        new LinkedHashMap<String, Object>(accessToken.getAdditionalInformation());

    info.put("companyId", user.getCompanyId().toHexString());
    info.put("teamId", user.getTeamId().toHexString());
    info.put("id", user.getId().toHexString());
    info.put("role", user.getRole().getId());

    DefaultOAuth2AccessToken customAccessToken = new DefaultOAuth2AccessToken(accessToken);
    customAccessToken.setAdditionalInformation(info);
    return super.enhance(customAccessToken, authentication);
  }
}
