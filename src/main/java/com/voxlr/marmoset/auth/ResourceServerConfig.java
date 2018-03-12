package com.voxlr.marmoset.auth;

import com.voxlr.marmoset.config.properties.JWTProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;

@Configuration
@EnableResourceServer
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableConfigurationProperties(JWTProperties.class)
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {
  @Autowired private ResourceServerTokenServices tokenServices;

  @Autowired private JWTProperties jwtProperties;

  @Autowired private TokenStore tokenStore;

  @Override
  public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
    resources
        .resourceId(jwtProperties.getResourceId())
        .tokenServices(tokenServices)
        .tokenStore(tokenStore);
  }

  @Override
  public void configure(HttpSecurity http) throws Exception {
    http.cors()
        .and()
        .authorizeRequests()
        .antMatchers("/actuator/**", "/api-docs/**", "/api/callback/**")
        .permitAll()
        .and()
        .requestMatcher(new OAuthRequestedMatcher())
        .anonymous()
        .disable()
        .authorizeRequests()
        .antMatchers(HttpMethod.OPTIONS)
        .permitAll()
        .antMatchers("/api/**")
        .authenticated();
  }

  private static class OAuthRequestedMatcher implements RequestMatcher {
    public boolean matches(HttpServletRequest request) {
      String auth = request.getHeader("Authorization");
      // Determine if the client request contained an OAuth Authorization
      boolean haveOauth2Token = (auth != null) && auth.startsWith("Bearer");
      boolean haveAccessToken = request.getParameter("access_token") != null;
      return haveOauth2Token || haveAccessToken;
    }
  }
}
