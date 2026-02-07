package com.scientia.mercatus.filter;

import com.scientia.mercatus.service.SessionService;
import com.scientia.mercatus.util.CookieUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
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
        if (!sessionService.validateSession(sessionId)) { // case: if invalid
            if (sessionId != null) {
                cookieUtil.deleteCookie(response);// case: if invalid but not null then delete
                // (must be in revoked list)
            }
            sessionId = sessionService.createSession();
            cookieUtil.addSessionCookie(response, sessionId);
        }
        request.setAttribute(SESSION_ATTRIBUTE, sessionId);//if valid then add an attribute to request
        filterChain.doFilter(request, response);

    }
}
