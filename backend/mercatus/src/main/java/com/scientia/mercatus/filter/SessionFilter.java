package com.scientia.mercatus.filter;

import com.scientia.mercatus.service.SessionService;
import com.scientia.mercatus.util.CookieUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Component
@RequiredArgsConstructor
public class SessionFilter extends OncePerRequestFilter {

    private final CookieUtil cookieUtil;
    private final SessionService sessionService;

    public static final String SESSION_ATTRIBUTE = "SESSION_ATTRIBUTE";
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {



        String sessionId = cookieUtil.getSessionId(request);
        if (!sessionService.validateSession(sessionId)) {
            if (sessionId != null) {
                cookieUtil.deleteCookie(response);
            }
            sessionId = sessionService.createSession();
            cookieUtil.addSessionCookie(response, sessionId);
        }
        request.setAttribute(SESSION_ATTRIBUTE, sessionId);
        filterChain.doFilter(request, response);

    }
}
