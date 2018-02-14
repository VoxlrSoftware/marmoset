package com.voxlr.marmoset.auth;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import com.voxlr.marmoset.config.properties.JWTProperties;

@Configuration
@EnableAuthorizationServer
@EnableConfigurationProperties(JWTProperties.class)
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    private final String SCOPE_READ = "read";
    private final String SCOPE_WRITE = "write";
    private final String GRANT_TYPE = "password";
    
    @Autowired
    private JWTProperties jwtProperties;
    
    @Autowired
    private TokenStore tokenStore;

    @Autowired
    private JwtAccessTokenConverter accessTokenConverter;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public void configure(ClientDetailsServiceConfigurer configurer) throws Exception {
	configurer
		.inMemory()
		.withClient(jwtProperties.getClientId())
		.secret(jwtProperties.getClientSecret())
		.authorizedGrantTypes(GRANT_TYPE)
		.scopes(SCOPE_READ, SCOPE_WRITE)
		.resourceIds(jwtProperties.getResourceId());
    }
    
    @Override
    public void configure(final AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
        oauthServer.tokenKeyAccess("permitAll()")
            .checkTokenAccess("isAuthenticated()");
    }
    
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
	TokenEnhancerChain enhancerChain = new TokenEnhancerChain();
	enhancerChain.setTokenEnhancers(Arrays.asList(accessTokenConverter));
	endpoints.tokenStore(tokenStore)
		.accessTokenConverter(accessTokenConverter)
		.tokenEnhancer(enhancerChain)
		.authenticationManager(authenticationManager);
    }
    
    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
	JwtAccessTokenConverter converter = new MarmosetTokenEnhancer();
	DefaultAccessTokenConverter defaultAccessTokenConverter = new DefaultAccessTokenConverter();
	defaultAccessTokenConverter.setUserTokenConverter(new MarmosetTokenConverter());
	converter.setAccessTokenConverter(defaultAccessTokenConverter);
	converter.setSigningKey(jwtProperties.getSigningKey());
	return converter;
    }
    
    @Bean
    public TokenStore tokenStore() {
	return new JwtTokenStore(accessTokenConverter());
    }
    
    @Bean
    @Primary
    public DefaultTokenServices tokenServices() {
	DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
	defaultTokenServices.setTokenStore(tokenStore());
	defaultTokenServices.setSupportRefreshToken(true);
	return defaultTokenServices;
    }
}
