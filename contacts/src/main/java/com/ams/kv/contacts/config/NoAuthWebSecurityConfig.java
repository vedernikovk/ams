package com.ams.kv.contacts.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

@Configuration
@ConditionalOnProperty(name="API_AUTH", havingValue="false")
public class NoAuthWebSecurityConfig extends ResourceServerConfigurerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(NoAuthWebSecurityConfig.class);

    @Override
    public void configure(HttpSecurity http) throws Exception {
        LOGGER.warn("Applying no auth configuration. Not for production use!");
        http.csrf()
            .disable()
            .authorizeRequests()
            .antMatchers("/**").permitAll();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return s -> null;
    }
}
