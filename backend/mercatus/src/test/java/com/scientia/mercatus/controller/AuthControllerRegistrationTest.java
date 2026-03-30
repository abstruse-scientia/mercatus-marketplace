package com.scientia.mercatus.controller;

import com.scientia.mercatus.dto.Auth.RegisterRequestDto;
import com.scientia.mercatus.dto.Auth.RegisterResponseDto;
import com.scientia.mercatus.entity.Role;
import com.scientia.mercatus.entity.RoleName;
import com.scientia.mercatus.entity.User;
import com.scientia.mercatus.exception.BusinessException;
import com.scientia.mercatus.exception.ErrorEnum;
import com.scientia.mercatus.security.jwt.JwtTokenProvider;
import com.scientia.mercatus.service.IRefreshTokenService;
import com.scientia.mercatus.service.IUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class AuthControllerRegistrationTest {

    @Mock
    private IUserService userService;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private IRefreshTokenService refreshTokenService;

    @InjectMocks
    private AuthController authController;

    private RegisterRequestDto validRegisterRequest;
    private User testUser;
    private User testAdmin;

    @BeforeEach
    void setUp() {
        validRegisterRequest = new RegisterRequestDto(
            "test@example.com",
            "SecurePass123!",
            "SecurePass123!",
            "testuser"
        );

        testUser = new User();
        testUser.setUserId(1L);
        testUser.setEmail("test@example.com");
        testUser.setUserName("testuser");
        testUser.setPasswordHash("$2a$10$hashed");
        
        Role userRole = new Role();
        userRole.setId(1L);
        userRole.setName(RoleName.ROLE_USER);
        Set<Role> userRoles = new HashSet<>();
        userRoles.add(userRole);
        testUser.setRoles(userRoles);

        testAdmin = new User();
        testAdmin.setUserId(2L);
        testAdmin.setEmail("admin@example.com");
        testAdmin.setUserName("superadmin");
        testAdmin.setPasswordHash("$2a$10$hashed");
        
        Role adminRole = new Role();
        adminRole.setId(2L);
        adminRole.setName(RoleName.ROLE_ADMIN);
        Set<Role> adminRoles = new HashSet<>();
        adminRoles.add(userRole);
        adminRoles.add(adminRole);
        testAdmin.setRoles(adminRoles);
    }

    @Test
    void testUserRegistration_Success() {
        when(userService.registerUser(anyString(), anyString(), anyString()))
                .thenReturn(testUser);
        when(jwtTokenProvider.generateJwtToken(any(User.class)))
                .thenReturn("jwt_token");
        when(refreshTokenService.createRefreshToken(any(User.class)))
                .thenReturn("refresh_token");

        ResponseEntity<?> response = authController.apiRegister(validRegisterRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof RegisterResponseDto);
        
        RegisterResponseDto responseDto = (RegisterResponseDto) response.getBody();
        assertEquals("User registered successfully", responseDto.message());
        assertEquals("test@example.com", responseDto.user().getEmail());
        assertEquals("ROLE_USER", responseDto.user().getRoles());
        assertNotNull(responseDto.jwtToken());
        assertNotNull(responseDto.refreshToken());

        verify(userService).registerUser("test@example.com", "SecurePass123!", "testuser");
    }

    @Test
    void testUserRegistration_DuplicateEmail() {
        when(userService.registerUser(anyString(), anyString(), anyString()))
                .thenThrow(new BusinessException(ErrorEnum.USER_EMAIL_ALREADY_EXISTS));

        ResponseEntity<?> response = authController.apiRegister(validRegisterRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testUserRegistration_ServerError() {
        when(userService.registerUser(anyString(), anyString(), anyString()))
                .thenThrow(new RuntimeException("Database error"));

        ResponseEntity<?> response = authController.apiRegister(validRegisterRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void testAdminRegistration_Success() {
        when(userService.registerAdmin(anyString(), anyString(), anyString()))
                .thenReturn(testAdmin);
        when(jwtTokenProvider.generateJwtToken(any(User.class)))
                .thenReturn("jwt_token");
        when(refreshTokenService.createRefreshToken(any(User.class)))
                .thenReturn("refresh_token");

        ResponseEntity<?> response = authController.apiRegisterAdmin(validRegisterRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof RegisterResponseDto);
        
        RegisterResponseDto responseDto = (RegisterResponseDto) response.getBody();
        assertEquals("Admin registered successfully", responseDto.message());
        assertEquals("admin@example.com", responseDto.user().getEmail());
        assertTrue(responseDto.user().getRoles().contains("ROLE_ADMIN"));
        assertTrue(responseDto.user().getRoles().contains("ROLE_USER"));
        assertNotNull(responseDto.jwtToken());
        assertNotNull(responseDto.refreshToken());

        verify(userService).registerAdmin("test@example.com", "SecurePass123!", "testuser");
    }

    @Test
    void testAdminRegistration_DuplicateEmail() {
        when(userService.registerAdmin(anyString(), anyString(), anyString()))
                .thenThrow(new BusinessException(ErrorEnum.USER_EMAIL_ALREADY_EXISTS));

        ResponseEntity<?> response = authController.apiRegisterAdmin(validRegisterRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testUserRegistration_JwtTokenGeneration() {
        when(userService.registerUser(anyString(), anyString(), anyString()))
                .thenReturn(testUser);
        when(jwtTokenProvider.generateJwtToken(testUser))
                .thenReturn("generated_jwt_token");
        when(refreshTokenService.createRefreshToken(testUser))
                .thenReturn("generated_refresh_token");

        ResponseEntity<?> response = authController.apiRegister(validRegisterRequest);

        RegisterResponseDto responseDto = (RegisterResponseDto) response.getBody();
        assertEquals("generated_jwt_token", responseDto.jwtToken());
        assertEquals("generated_refresh_token", responseDto.refreshToken());

        verify(jwtTokenProvider).generateJwtToken(testUser);
        verify(refreshTokenService).createRefreshToken(testUser);
    }

    @Test
    void testAdminRegistration_BothRolesAssigned() {
        when(userService.registerAdmin(anyString(), anyString(), anyString()))
                .thenReturn(testAdmin);
        when(jwtTokenProvider.generateJwtToken(any(User.class)))
                .thenReturn("jwt_token");
        when(refreshTokenService.createRefreshToken(any(User.class)))
                .thenReturn("refresh_token");

        ResponseEntity<?> response = authController.apiRegisterAdmin(validRegisterRequest);

        RegisterResponseDto responseDto = (RegisterResponseDto) response.getBody();
        String roles = responseDto.user().getRoles();
        
        assertTrue(roles.contains("ROLE_ADMIN"), "Admin should have ROLE_ADMIN");
        assertTrue(roles.contains("ROLE_USER"), "Admin should have ROLE_USER");
    }
}

