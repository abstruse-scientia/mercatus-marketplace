package com.scientia.mercatus.service.impl;

import com.scientia.mercatus.entity.Role;
import com.scientia.mercatus.entity.User;
import com.scientia.mercatus.repository.UserRepository;
import com.scientia.mercatus.security.UserIdentifierService;
import com.scientia.mercatus.service.IUserManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserManagementServiceImpl implements IUserManagementService {

    private final UserRepository userRepository;
    private final UserIdentifierService userIdentifierService;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void updateUserRoles(Long userId, Set<Role> newRoles) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        if (newRoles != null && !newRoles.equals(user.getRoles())) {
            log.info("Updating roles for user: {}", userId);
            user.setRoles(newRoles);
            userRepository.save(user);
            
            if (user.getOpaqueIdentifier() != null) {
                userIdentifierService.invalidateUserCache(user.getOpaqueIdentifier());
                log.debug("Cache invalidated for user after role update: {}", userId);
            }
        }
    }

    @Override
    @Transactional
    public void deactivateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        log.info("Deactivating user: {}", userId);
        user.setActive(false);
        userRepository.save(user);
        
        if (user.getOpaqueIdentifier() != null) {
            userIdentifierService.invalidateUserCache(user.getOpaqueIdentifier());
            log.debug("Cache invalidated for deactivated user: {}", userId);
        }
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        String opaqueId = user.getOpaqueIdentifier();
        
        log.info("Deleting user: {}", userId);
        userRepository.delete(user);
        
        if (opaqueId != null) {
            userIdentifierService.invalidateUserCache(opaqueId);
            log.debug("Cache invalidated for deleted user: {}", userId);
        }
    }

    @Override
    @Transactional
    public void updateUserPassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        log.info("Updating password for user: {}", userId);
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        if (user.getOpaqueIdentifier() != null) {
            userIdentifierService.invalidateUserCache(user.getOpaqueIdentifier());
            log.debug("Cache invalidated for user after password change: {}", userId);
        }
    }
}

