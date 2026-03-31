package com.scientia.mercatus.controller;

import com.scientia.mercatus.dto.*;
import com.scientia.mercatus.dto.Auth.LoginRequestDto;
import com.scientia.mercatus.dto.Auth.LoginResponseDto;
import com.scientia.mercatus.dto.Auth.RefreshTokenRequestDto;
import com.scientia.mercatus.dto.Auth.RefreshTokenResponseDto;
import com.scientia.mercatus.dto.Auth.RegisterRequestDto;
import com.scientia.mercatus.dto.Auth.RegisterResponseDto;
import com.scientia.mercatus.entity.User;
import com.scientia.mercatus.exception.BusinessException;
import com.scientia.mercatus.mapper.UserMapper;
import com.scientia.mercatus.security.UserIdentifierService;
import com.scientia.mercatus.security.jwt.JwtTokenProvider;
import com.scientia.mercatus.service.IRefreshTokenService;
import com.scientia.mercatus.service.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final IRefreshTokenService iRefreshTokenService;
    private final IUserService iUserService;
    private final UserIdentifierService userIdentifierService;
    private final UserMapper userMapper;

    @PostMapping("/login")
    public ResponseEntity<?> apiLogin(@RequestBody LoginRequestDto loginRequestDto) {
        try{
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequestDto.userEmail(), loginRequestDto.password())
            );
            var loggedInUser = (User) authentication.getPrincipal();
            String jwtToken = jwtTokenProvider.generateJwtToken(loggedInUser);
            var userDto = userMapper.toUserDto(loggedInUser);
            String refreshToken = iRefreshTokenService.createRefreshToken(loggedInUser);
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
        RefreshTokenResponseDto response = iRefreshTokenService.rotateRefreshToken
                (refreshTokenRequestDto.refreshToken());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/register")
    public ResponseEntity<?> apiRegister(@Valid @RequestBody RegisterRequestDto registerRequestDto) {
            User newUser = iUserService.registerUser(
                registerRequestDto.email(),
                registerRequestDto.password(),
                registerRequestDto.userName()
            );

            String jwtToken = jwtTokenProvider.generateJwtToken(newUser);
            String refreshToken = iRefreshTokenService.createRefreshToken(newUser);
            var userDto = userMapper.toUserDto(newUser);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new RegisterResponseDto(
                        "User registered successfully",
                        userDto,
                        jwtToken,
                        refreshToken
                    ));
    }

    @PostMapping("/admin/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> apiRegisterAdmin(@Valid @RequestBody RegisterRequestDto registerRequestDto) {
        try {
            User newAdmin = iUserService.registerAdmin(
                registerRequestDto.email(),
                registerRequestDto.password(),
                registerRequestDto.userName()
            );

            String jwtToken = jwtTokenProvider.generateJwtToken(newAdmin);
            String refreshToken = iRefreshTokenService.createRefreshToken(newAdmin);
            var userDto = userMapper.toUserDto(newAdmin);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new RegisterResponseDto(
                        "Admin registered successfully",
                        userDto,
                        jwtToken,
                        refreshToken
                    ));

        } catch (BusinessException ex) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
        } catch (Exception ex) {
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Admin registration failed");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> apiLogout() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof User user) {
                if (user.getOpaqueIdentifier() != null) {
                    userIdentifierService.invalidateUserCache(user.getOpaqueIdentifier());
                }
            }
            SecurityContextHolder.clearContext();
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new LoginResponseDto("Logged out successfully", null, null, null));
        } catch (Exception ex) {
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Logout failed");
        }
    }

    private ResponseEntity<?> buildErrorResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(new LoginResponseDto(message, null, null, null));
    }


}
