package com.scientia.mercatus.util;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CookieUtil {
    public String getSessionId(HttpServletRequest request) {
        if (request.getCookies() != null ) {
            for (Cookie cookie: request.getCookies()) {
                if (cookie.getName().equals("SESSION_ID")) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
    public void addSessionCookie(HttpServletResponse response, String sessionId) {
        Cookie newCookie = new Cookie("SESSION_ID", sessionId);
        newCookie.setPath("/");
        newCookie.setHttpOnly(true);
        newCookie.setMaxAge(60 * 60 * 24 * 7);
        response.addCookie(newCookie);
    }

    public void deleteCookie(HttpServletResponse response) {
        Cookie newCookie = new Cookie("SESSION_ID", "");
        newCookie.setPath("/");
        newCookie.setMaxAge(0);
        response.addCookie(newCookie);
    }
}
