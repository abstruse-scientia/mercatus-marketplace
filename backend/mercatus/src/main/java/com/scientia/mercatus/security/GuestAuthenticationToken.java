package com.scientia.mercatus.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

public class GuestAuthenticationToken extends AbstractAuthenticationToken {

    private final String sessionId;
    public GuestAuthenticationToken(String sessionId) {
        super(List.of(new SimpleGrantedAuthority("ROLE_GUEST")));
        this.sessionId = sessionId;
        setAuthenticated(true);
    }
    @Override
    public Object getCredentials() {
        return sessionId;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }
}
