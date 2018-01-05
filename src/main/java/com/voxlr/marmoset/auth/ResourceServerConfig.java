package com.voxlr.marmoset.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;

import com.voxlr.marmoset.config.properties.JWTProperties;

@Configuration
@EnableResourceServer
@EnableConfigurationProperties(JWTProperties.class)
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {
    @Autowired
    private ResourceServerTokenServices tokenServices;

    @Autowired
    private JWTProperties jwtProperties;
    
    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.resourceId(jwtProperties.getResourceId()).tokenServices(tokenServices);
    }
    
    @Override
    public void configure(HttpSecurity http) throws Exception {
	http
	.cors()
	.and()
        .requestMatchers()
        .and()
        .authorizeRequests()
        .antMatchers(
        	"/actuator/**",
        	"/api-docs/**",
        	"/api/callback/**").permitAll()
        .antMatchers("/api/**").authenticated();
    }

}
