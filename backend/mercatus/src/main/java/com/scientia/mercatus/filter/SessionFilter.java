package com.scientia.mercatus.filter;

import com.scientia.mercatus.security.GuestAuthenticationToken;
import com.scientia.mercatus.service.ISessionService;
import com.scientia.mercatus.util.CookieUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Component
@RequiredArgsConstructor
public class SessionFilter extends OncePerRequestFilter {

    private final CookieUtil cookieUtil;
    private final ISessionService sessionService;

    public static final String SESSION_ATTRIBUTE = "SESSION_ATTRIBUTE";
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {


        //When to set guest authentication token when session Id is null ? or invalid
        /*
        No it should be created either ways when session Id is invalid or valid, it should be created
        when auth is null , its null when user is not authenticated either as guest or logged in
         */
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String sessionId = cookieUtil.getSessionId(request);
        //if invalid session
        if (!sessionService.validateSession(sessionId)) {
            // if session id is invalid due to it being part of revoked session
            if (sessionId != null) {
                cookieUtil.deleteCookie(response);
            }
            sessionId = sessionService.createSession();
            cookieUtil.addSessionCookie(response, sessionId);
        }

        if (auth == null) {
            GuestAuthenticationToken guestToken = new GuestAuthenticationToken(sessionId);
            SecurityContextHolder.getContext().setAuthentication(guestToken);
        }
        request.setAttribute(SESSION_ATTRIBUTE, sessionId);
        filterChain.doFilter(request, response);
    }
}
