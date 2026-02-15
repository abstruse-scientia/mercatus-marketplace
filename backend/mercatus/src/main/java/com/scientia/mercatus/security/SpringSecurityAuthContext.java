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

        if (authentication == null || !authentication.isAuthenticated() ||
                authentication instanceof AnonymousAuthenticationToken) {
            throw new IllegalStateException("No authenticated user");
        }

        User user =
                (User) authentication.getPrincipal();

        return user.getUserId();
    }

    @Override
    public Long getCurrentUserIdOrNull() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated())  {
            return null;
        }
        if (authentication.getPrincipal() instanceof User user) {
            return user.getUserId();
        }
        return null;
    }
}
