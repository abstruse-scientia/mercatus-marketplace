package com.scientia.mercatus.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("auditorAwareImpl")
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<User> getCurrentAuditor() {

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    }
}
