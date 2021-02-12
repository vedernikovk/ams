package com.ams.kv.contacts.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;

public class RoleTokenConverter extends DefaultAccessTokenConverter {
	
	private static Logger log = LoggerFactory.getLogger(RoleTokenConverter.class.getName());

    @Override
    public OAuth2Authentication extractAuthentication(Map<String, ?> tokenMap) {
        List<GrantedAuthority> authorities = extractRoles(tokenMap);

        OAuth2Authentication authentication = super.extractAuthentication(tokenMap);
        OAuth2Request oAuth2Request = authentication.getOAuth2Request();

        OAuth2Request request =
                new OAuth2Request(oAuth2Request.getRequestParameters(), oAuth2Request.getClientId(), authorities, true, oAuth2Request.getScope(),
                        null, null, null, null);

        Authentication usernamePasswordAuthentication = new UsernamePasswordAuthenticationToken(authentication.getPrincipal(), "N/A", authorities);
        return new OAuth2Authentication(request, usernamePasswordAuthentication);
    }

    private List<GrantedAuthority> extractRoles(Map<String, ?> tokenMap) {
        List<Object> authorities = new ArrayList<>();

        Object realmAccessNode = tokenMap.get("realm_access");
        if (realmAccessNode instanceof Map) {
            Object rolesNode = ((Map) realmAccessNode).get("roles");
            if (rolesNode instanceof List) {
                ((List<?>) rolesNode)
                        .stream()
                        .map(this::roleNameToSpringRole)
                        .forEach(authorities::add);
            }
            if (rolesNode instanceof String) {
                authorities.add(roleNameToSpringRole(rolesNode));
            }
        }

        log.debug("groups {}", tokenMap.get("groups"));
        log.debug("hasgroups {}", tokenMap.get("hasgroups"));

        return AuthorityUtils.createAuthorityList((String[]) authorities.toArray(new String[0]));
    }

    private String roleNameToSpringRole(Object name) {
        return "ROLE_" + name.toString();
    }    
}
