package com.scientia.mercatus.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scientia.mercatus.dto.Auth.RegisterRequestDto;
import com.scientia.mercatus.entity.Role;
import com.scientia.mercatus.entity.RoleName;
import com.scientia.mercatus.entity.User;
import com.scientia.mercatus.repository.RoleRepository;
import com.scientia.mercatus.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthControllerRegistrationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private RegisterRequestDto validRegisterRequest;
    private Role userRole;
    private Role adminRole;

    @BeforeEach
    void setUp() {
        validRegisterRequest = new RegisterRequestDto(
            "newuser@example.com",
            "SecurePass123!",
            "SecurePass123!",
            "newuser"
        );

        userRole = new Role();
        userRole.setName(RoleName.ROLE_USER);
        if (roleRepository.findByName(RoleName.ROLE_USER).isEmpty()) {
            roleRepository.save(userRole);
        } else {
            userRole = roleRepository.findByName(RoleName.ROLE_USER).get();
        }

        adminRole = new Role();
        adminRole.setName(RoleName.ROLE_ADMIN);
        if (roleRepository.findByName(RoleName.ROLE_ADMIN).isEmpty()) {
            roleRepository.save(adminRole);
        } else {
            adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN).get();
        }
    }

    @Test
    void testUserRegistration_EndToEnd() throws Exception {
        RegisterRequestDto request = new RegisterRequestDto(
            "testuser@example.com",
            "SecurePass123!",
            "SecurePass123!",
            "testuser"
        );

        MvcResult result = mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("User registered successfully"))
                .andExpect(jsonPath("$.user.email").value("testuser@example.com"))
                .andExpect(jsonPath("$.user.username").value("testuser"))
                .andExpect(jsonPath("$.user.roles").value("ROLE_USER"))
                .andExpect(jsonPath("$.jwtToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty())
                .andReturn();

        User createdUser = userRepository.findByEmail("testuser@example.com").orElse(null);
        assertNotNull(createdUser);
        assertEquals("testuser@example.com", createdUser.getEmail());
        assertEquals("testuser", createdUser.getUserName());
        assertTrue(passwordEncoder.matches("SecurePass123!", createdUser.getPasswordHash()));
        assertEquals(1, createdUser.getRoles().size());
        assertTrue(createdUser.getRoles().stream()
                .anyMatch(r -> r.getName() == RoleName.ROLE_USER));
    }

    @Test
    void testUserRegistration_DuplicateEmail() throws Exception {
        User existingUser = new User();
        existingUser.setEmail("existing@example.com");
        existingUser.setUserName("existing");
        existingUser.setPasswordHash(passwordEncoder.encode("SecurePass123!"));
        existingUser.setOpaqueIdentifier(UUID.randomUUID().toString());
        existingUser.getRoles().add(userRole);
        userRepository.save(existingUser);

        RegisterRequestDto request = new RegisterRequestDto(
            "existing@example.com",
            "SecurePass123!",
            "SecurePass123!",
            "newuser"
        );

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorMessage").value(containsString("Email is already registered")));
    }

    @Test
    void testUserRegistration_InvalidEmail() throws Exception {
        RegisterRequestDto request = new RegisterRequestDto(
            "invalid-email",
            "SecurePass123!",
            "SecurePass123!",
            "testuser"
        );

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUserRegistration_WeakPassword() throws Exception {
        RegisterRequestDto request = new RegisterRequestDto(
            "test@example.com",
            "weak",
            "weak",
            "testuser"
        );

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUserRegistration_PasswordMismatch() throws Exception {
        RegisterRequestDto request = new RegisterRequestDto(
            "test@example.com",
            "SecurePass123!",
            "DifferentPass123!",
            "testuser"
        );

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerAdmin_withInvalidToken_returns401() throws Exception {
        User adminUser = new User();
        adminUser.setEmail("admin@example.com");
        adminUser.setUserName("admin");
        adminUser.setPasswordHash(passwordEncoder.encode("SecurePass123!"));
        adminUser.setOpaqueIdentifier(UUID.randomUUID().toString());
        adminUser.getRoles().add(userRole);
        adminUser.getRoles().add(adminRole);
        userRepository.save(adminUser);

        String adminJwt = "valid_admin_jwt";

        RegisterRequestDto request = new RegisterRequestDto(
            "newadmin@example.com",
            "SecurePass123!",
            "SecurePass123!",
            "newadmin"
        );

        mockMvc.perform(post("/api/v1/auth/admin/register")
                .header("Authorization", "Bearer " + adminJwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }


    @Test
    void testUserRegistration_PasswordHashed() throws Exception {
        String plainPassword = "SecurePass123!";
        RegisterRequestDto request = new RegisterRequestDto(
            "hashtest@example.com",
            plainPassword,
            plainPassword,
            "hashtest"
        );

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        User registeredUser = userRepository.findByEmail("hashtest@example.com").orElse(null);
        assertNotNull(registeredUser);
        assertNotEquals(plainPassword, registeredUser.getPasswordHash());
        assertTrue(passwordEncoder.matches(plainPassword, registeredUser.getPasswordHash()));
    }

    @Test
    void testUserRegistration_MultipleUsers() throws Exception {
        for (int i = 0; i < 3; i++) {
            RegisterRequestDto request = new RegisterRequestDto(
                "user" + i + "@example.com",
                "SecurePass123!",
                "SecurePass123!",
                "user" + i
            );

            mockMvc.perform(post("/api/v1/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());
        }


        assertEquals(5, userRepository.count());
    }

    @Test
    void testUserRegistration_JwtTokenPresent() throws Exception {
        RegisterRequestDto request = new RegisterRequestDto(
            "jwttest@example.com",
            "SecurePass123!",
            "SecurePass123!",
            "jwttest"
        );

        MvcResult result = mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        assertTrue(responseBody.contains("jwtToken"));
        assertTrue(responseBody.contains("refreshToken"));
    }
}

