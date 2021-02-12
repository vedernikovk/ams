package com.ams.kv.contacts.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.jwk.JwkTokenStore;
import org.springframework.security.web.savedrequest.RequestCacheAwareFilter;

@Configuration
public class SecurityConfigurer extends ResourceServerConfigurerAdapter {
	
    @Value("${security.oauth2.resource.jwk.key-set-uri}")
    private String keySetUri;
    
	@Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
         resources.resourceId("account");
    }
	
    @Override
    public void configure(HttpSecurity http) throws Exception {
    	http
	    	.sessionManagement()
	    	.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    	
    	http.cors().and().csrf().disable();
    	
        http
        	.authorizeRequests()
            .antMatchers("/actuator/**").permitAll()
            .anyRequest().hasRole("contact-admin");
        
        http.addFilterBefore(new AppAuthenticationFilter(), RequestCacheAwareFilter.class);
    }
    
    @Bean
    public TokenStore tokenStore() {
    	
        List<String> keySetUris = new ArrayList<String>();
        keySetUris.add(keySetUri);

        return new JwkTokenStore(keySetUris, new RoleTokenConverter(), null);
    }    
}
