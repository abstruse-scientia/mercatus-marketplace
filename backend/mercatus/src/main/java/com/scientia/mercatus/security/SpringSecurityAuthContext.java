package com.scientia.mercatus.security;

import com.scientia.mercatus.entity.User;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SpringSecurityAuthContext implements AuthContext {

    @Override
    public Long getCurrentUserId() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            throw new IllegalStateException("Authentication object is null");
        }
        if (authentication.getPrincipal() instanceof User user) {
            return user.getUserId();
        }

        throw new IllegalStateException("No authenticated user in security context.");
    }

    @Override
    public Long getCurrentUserIdOrNull() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null)  {
            return null;
        }
        if (authentication.getPrincipal() instanceof User user) {
            return user.getUserId();
        }
        return null;
    }
}
