package com.scientia.mercatus.security.jwt;

import com.scientia.mercatus.entity.Role;
import com.scientia.mercatus.entity.User;
import com.scientia.mercatus.security.UserIdentifierService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;


import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenValidatorFilter extends OncePerRequestFilter {

    private final UserIdentifierService userIdentifierService;
    private final JwtTokenValidator jwtTokenValidator;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException{
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer")){
                String token = authHeader.substring(7);
                
                Claims claims = jwtTokenValidator.validateAndParseClaims(token);
                
                User user = null;
                
                if (claims.containsKey("uid")) {
                    String opaqueId = String.valueOf(claims.get("uid"));
                    log.debug("New format token - validating with opaque identifier: {}", opaqueId);
                    
                    user = userIdentifierService.getUserByOpaqueIdentifier(opaqueId)
                            .orElseThrow(() -> {
                                log.warn("User not found for opaque identifier: {}", opaqueId);
                                return new RuntimeException("User not found");
                            });
                            
                } else if (claims.containsKey("email")) {
                    String userEmail = String.valueOf(claims.get("email"));
                    log.warn("DEPRECATED: Old format token detected - user should re-authenticate. Email: {}", userEmail);
                    throw new BadCredentialsException("Old token format. Please re-authenticate to get updated token.");
                    
                } else {
                    throw new BadCredentialsException("Invalid token format: missing required claims");
                }

                Set<Role> roles = user.getRoles();
                List<SimpleGrantedAuthority> authorities = roles.stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName().name())).toList();
                Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("Authentication set for user: {}", user.getUserId());
            }

        } catch(ExpiredJwtException ex) {
            log.warn("Expired JWT token received");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token has expired");
            return;
        } catch (BadCredentialsException ex) {
            log.warn("Bad credentials in JWT validation: {}", ex.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(ex.getMessage());
            return;
        } catch (JwtException ex) {
            log.warn("JWT validation failed: {}", ex.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid token");
            return;
        }
        filterChain.doFilter(request, response);
    }


}
