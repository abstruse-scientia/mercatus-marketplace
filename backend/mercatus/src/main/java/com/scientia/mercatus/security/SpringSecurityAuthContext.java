package com.scientia.mercatus.security;

import com.scientia.mercatus.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SpringSecurityAuthContext implements AuthContext {

    @Override
    public Long getCurrentUserId() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No authenticated user");
        }

        User user =
                (User) authentication.getPrincipal();

        return user.getUserId();
    }
}
