package com.scientia.mercatus.controller;

import com.scientia.mercatus.dto.*;

import com.scientia.mercatus.dto.Auth.LoginRequestDto;
import com.scientia.mercatus.dto.Auth.LoginResponseDto;
import com.scientia.mercatus.dto.Auth.RefreshTokenRequestDto;
import com.scientia.mercatus.dto.Auth.RefreshtTokenResponseDto;
import com.scientia.mercatus.entity.User;


import com.scientia.mercatus.security.jwt.JwtTokenProvider;
import com.scientia.mercatus.service.IRefreshTokenService;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.stream.Collectors;

@Profile("!test")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final IRefreshTokenService iRefreshTokenService;
    @PostMapping("/login")
    public ResponseEntity<?> apiLogin(@RequestBody LoginRequestDto loginRequestDto) {
        try{
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequestDto.userEmail(), loginRequestDto.password())
            );
            var userDto = new UserDto();
            var loggedInUser = (User) authentication.getPrincipal();
            String jwtToken = jwtTokenProvider.generateJwtToken(loggedInUser);
            BeanUtils.copyProperties(loggedInUser, userDto);
            String refreshToken = iRefreshTokenService.createRefreshToken(loggedInUser);
            userDto.setRoles(authentication.getAuthorities().stream().map(
                    GrantedAuthority::getAuthority).collect(Collectors.joining(",")));
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new LoginResponseDto(HttpStatus.OK.getReasonPhrase(), userDto, jwtToken, refreshToken));
        } catch(BadCredentialsException ex) {
            return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Invalid username and password");
        }catch(AuthenticationException ex) {
            return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Authentication Failed");
        }catch(Exception ex) {
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error has occurred");
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequestDto refreshTokenRequestDto) {
        RefreshtTokenResponseDto response = iRefreshTokenService.rotateRefreshToken
                (refreshTokenRequestDto.refreshToken());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }



    private ResponseEntity<?> buildErrorResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(new LoginResponseDto(message, null, null, null));
    }
}
