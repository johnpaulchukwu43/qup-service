package com.jworks.qup.service.security;

import java.util.HashMap;
import java.util.Map;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;

/**
 * @author bodmas
 * @since Oct 2, 2021.
 */
public class AuthenticationMocks {

    private AuthenticationMocks() {
    }

    public static Authentication adminAuthentication() {
        User principal = new User("Admin 1", "secret", true, true, true, true,
                AuthorityUtils.createAuthorityList("USER", "ADMIN"));
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                principal, principal.getPassword(), principal.getAuthorities());

        Map<String, Object> details = new HashMap<>();
        authentication.setDetails(details);

        return authentication;
    }
}
