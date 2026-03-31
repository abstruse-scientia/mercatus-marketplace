package com.scientia.mercatus.service;

import com.scientia.mercatus.entity.Role;
import com.scientia.mercatus.entity.RoleName;
import com.scientia.mercatus.entity.User;
import com.scientia.mercatus.exception.BusinessException;
import com.scientia.mercatus.exception.ErrorEnum;
import com.scientia.mercatus.repository.RoleRepository;
import com.scientia.mercatus.repository.UserRepository;
import com.scientia.mercatus.service.impl.IUserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class UserServiceRegistrationTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private IUserServiceImpl userService;

    private Role userRole;
    private Role adminRole;

    @BeforeEach
    void setUp() {
        userRole = new Role();
        userRole.setId(1L);
        userRole.setName(RoleName.ROLE_USER);

        adminRole = new Role();
        adminRole.setId(2L);
        adminRole.setName(RoleName.ROLE_ADMIN);
    }

    @Test
    void testRegisterUser_Success() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("SecurePass123!")).thenReturn("$2a$10$hashed");
        when(roleRepository.findByName(RoleName.ROLE_USER)).thenReturn(Optional.of(userRole));

        User savedUser = new User();
        savedUser.setUserId(1L);
        savedUser.setEmail("test@example.com");
        savedUser.setUserName("testuser");
        savedUser.setPasswordHash("$2a$10$hashed");
        savedUser.setOpaqueIdentifier(UUID.randomUUID().toString());
        savedUser.getRoles().add(userRole);

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = userService.registerUser("test@example.com", "SecurePass123!", "testuser");

        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        assertEquals("test@example.com", result.getEmail());
        assertEquals("testuser", result.getUserName());
        assertEquals("$2a$10$hashed", result.getPasswordHash());
        assertEquals(1, result.getRoles().size());
        assertTrue(result.getRoles().contains(userRole));

        verify(userRepository).existsByEmail("test@example.com");
        verify(passwordEncoder).encode("SecurePass123!");
        verify(roleRepository).findByName(RoleName.ROLE_USER);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testRegisterUser_DuplicateEmail() {
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        assertThrows(BusinessException.class, () ->
                userService.registerUser("existing@example.com", "SecurePass123!", "user")
        );

        verify(userRepository).existsByEmail("existing@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRegisterUser_RoleNotFound() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("SecurePass123!")).thenReturn("$2a$10$hashed");
        when(roleRepository.findByName(RoleName.ROLE_USER)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () ->
                userService.registerUser("test@example.com", "SecurePass123!", "testuser")
        );

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRegisterUser_PasswordHashing() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("SecurePass123!")).thenReturn("$2a$10$hashed");
        when(roleRepository.findByName(RoleName.ROLE_USER)).thenReturn(Optional.of(userRole));

        User savedUser = new User();
        savedUser.setPasswordHash("$2a$10$hashed");
        savedUser.setOpaqueIdentifier(UUID.randomUUID().toString());
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        userService.registerUser("test@example.com", "SecurePass123!", "testuser");

        verify(passwordEncoder).encode("SecurePass123!");
    }

    @Test
    void testRegisterAdmin_Success() {
        when(userRepository.existsByEmail("admin@example.com")).thenReturn(false);
        when(passwordEncoder.encode("SecurePass123!")).thenReturn("$2a$10$hashed");
        when(roleRepository.findByName(RoleName.ROLE_USER)).thenReturn(Optional.of(userRole));
        when(roleRepository.findByName(RoleName.ROLE_ADMIN)).thenReturn(Optional.of(adminRole));

        User savedAdmin = new User();
        savedAdmin.setUserId(2L);
        savedAdmin.setEmail("admin@example.com");
        savedAdmin.setUserName("admin");
        savedAdmin.setPasswordHash("$2a$10$hashed");
        savedAdmin.setOpaqueIdentifier(UUID.randomUUID().toString());
        savedAdmin.getRoles().add(userRole);
        savedAdmin.getRoles().add(adminRole);

        when(userRepository.save(any(User.class))).thenReturn(savedAdmin);

        User result = userService.registerAdmin("admin@example.com", "SecurePass123!", "admin");

        assertNotNull(result);
        assertEquals(2L, result.getUserId());
        assertEquals("admin@example.com", result.getEmail());
        assertEquals(2, result.getRoles().size());
        assertTrue(result.getRoles().contains(userRole));
        assertTrue(result.getRoles().contains(adminRole));

        verify(roleRepository).findByName(RoleName.ROLE_USER);
        verify(roleRepository).findByName(RoleName.ROLE_ADMIN);
    }

    @Test
    void testRegisterAdmin_DuplicateEmail() {
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        assertThrows(BusinessException.class, () ->
                userService.registerAdmin("existing@example.com", "SecurePass123!", "admin")
        );

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRegisterAdmin_RoleUserNotFound() {
        when(userRepository.existsByEmail("admin@example.com")).thenReturn(false);
        when(passwordEncoder.encode("SecurePass123!")).thenReturn("$2a$10$hashed");
        when(roleRepository.findByName(RoleName.ROLE_USER)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () ->
                userService.registerAdmin("admin@example.com", "SecurePass123!", "admin")
        );

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRegisterAdmin_RoleAdminNotFound() {
        when(userRepository.existsByEmail("admin@example.com")).thenReturn(false);
        when(passwordEncoder.encode("SecurePass123!")).thenReturn("$2a$10$hashed");
        when(roleRepository.findByName(RoleName.ROLE_USER)).thenReturn(Optional.of(userRole));
        when(roleRepository.findByName(RoleName.ROLE_ADMIN)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () ->
                userService.registerAdmin("admin@example.com", "SecurePass123!", "admin")
        );

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRegisterAdmin_BothRolesAssigned() {
        when(userRepository.existsByEmail("admin@example.com")).thenReturn(false);
        when(passwordEncoder.encode("SecurePass123!")).thenReturn("$2a$10$hashed");
        when(roleRepository.findByName(RoleName.ROLE_USER)).thenReturn(Optional.of(userRole));
        when(roleRepository.findByName(RoleName.ROLE_ADMIN)).thenReturn(Optional.of(adminRole));

        User savedAdmin = new User();
        savedAdmin.setOpaqueIdentifier(UUID.randomUUID().toString());
        savedAdmin.getRoles().add(userRole);
        savedAdmin.getRoles().add(adminRole);

        when(userRepository.save(any(User.class))).thenReturn(savedAdmin);

        User result = userService.registerAdmin("admin@example.com", "SecurePass123!", "admin");

        assertEquals(2, result.getRoles().size());
    }
}

