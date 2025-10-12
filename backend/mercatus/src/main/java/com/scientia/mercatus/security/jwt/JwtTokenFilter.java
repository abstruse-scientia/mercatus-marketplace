package com.scientia.mercatus.security.jwt;

import com.scientia.mercatus.entity.Role;
import com.scientia.mercatus.entity.User;
import com.scientia.mercatus.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtKeyProvider jwtkeyProvider;
    private final UserRepository userRepository;
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();
    private final List<String> publicpaths;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException{
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer")){
                String token = authHeader.substring(7);
                Claims claims = Jwts.parser().verifyWith(jwtkeyProvider.getSecretKey())
                        .build().parseSignedClaims(token).getPayload();
                String userEmail = String.valueOf(claims.get("email"));
                User user = userRepository.findByemail(userEmail).orElseThrow(()->
                        new RuntimeException("User not found with email: " + userEmail));
                Set<Role> roles = user.getRoles();
                List<SimpleGrantedAuthority> authorities = roles.stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName())).toList();
                Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

        } catch(ExpiredJwtException ex) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Tokeh has expired");
            return;
        } catch (BadCredentialsException ex) {
            throw new BadCredentialsException("Bad credentials");
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String requestPath = request.getRequestURI();
        return publicpaths.stream().anyMatch(publicPath -> antPathMatcher.match(publicPath, requestPath));
    }


}
