package com.jworks.qup.service.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;

/**
 * @author bodmas
 * @since Oct 2, 2021.
 */
public class MockSecurityContext implements SecurityContext {

    private static final long serialVersionUID = 1L;

    private Authentication authentication;

    public MockSecurityContext(Authentication authentication) {
        this.authentication = authentication;
    }

    @Override
    public Authentication getAuthentication() {
        return authentication;
    }

    @Override
    public void setAuthentication(Authentication authentication) {
        this.authentication = authentication;
    }
}
