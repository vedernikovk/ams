package com.ams.kv.contacts.config;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

public class AppAuthenticationFilter implements Filter {

	private static Logger log = LoggerFactory.getLogger(AppAuthenticationFilter.class.getName());
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
		
        if (SecurityContextHolder.getContext().getAuthentication() instanceof OAuth2Authentication) {
            OAuth2Authentication oauth2authentication = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
            
            log.debug("Authorities {}", oauth2authentication.getAuthorities());
        }

        filterChain.doFilter(request, response);
	}

	@Override
	public void destroy() {
	}
}
