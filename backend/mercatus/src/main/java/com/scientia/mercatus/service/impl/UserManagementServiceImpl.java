package com.scientia.mercatus.service.impl;

import com.scientia.mercatus.entity.Role;
import com.scientia.mercatus.entity.User;
import com.scientia.mercatus.exception.BusinessException;
import com.scientia.mercatus.exception.ErrorEnum;
import com.scientia.mercatus.messaging.EmailEvent;
import com.scientia.mercatus.messaging.EmailEventPayloadUtility;
import com.scientia.mercatus.messaging.EmailPublisher;
import com.scientia.mercatus.repository.UserRepository;
import com.scientia.mercatus.security.UserIdentifierService;
import com.scientia.mercatus.service.IUserManagementService;
import com.scientia.mercatus.util.ISecureTokenUtil;
import com.scientia.mercatus.util.VerificationServiceUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserManagementServiceImpl implements IUserManagementService {

    private static final int ACCESS_TOKEN_TTL = 5;

    private final UserRepository userRepository;
    private final UserIdentifierService userIdentifierService;
    private final PasswordEncoder passwordEncoder;
    private final StringRedisTemplate stringRedisTemplate;
    private final VerificationServiceUtil verificationService;
    private final ISecureTokenUtil secureTokenUtil;
    private final EmailEventPayloadUtility payload;
    private final EmailPublisher emailPublisher;





    @Override
    public void forgotUserPassword(String email) {
        if (email == null || email.isEmpty()) {
            throw new BusinessException(ErrorEnum.INVALID_REQUEST);
        }
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new BusinessException(ErrorEnum.USER_NOT_FOUND));
        String accessToken = UUID.randomUUID().toString();
        String hashedToken = secureTokenUtil.hashToken(accessToken);
        stringRedisTemplate.opsForValue().set(hashedToken,
                email, ACCESS_TOKEN_TTL,
                TimeUnit.MINUTES);
        String verificationUrl = verificationService.generateVerificationLink(accessToken);
        EmailEvent event = payload.forForgotPassword(user, verificationUrl);
        emailPublisher.publishEmail(event);

    }

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

    @Override
    @Transactional
    public void resetPassword(String accessToken, String newPassword) {
        if (accessToken == null || newPassword == null) {
            throw new BusinessException(ErrorEnum.INVALID_REQUEST);
        }
        String hashedToken = secureTokenUtil.hashToken(accessToken);
        // hashedTokenValue is the email id.
        // Redis stores the accessToken's hash value as Key(K) and email id as Value (V)
        // Hence, (K) accessTokenHash:(V) emailId
        String hashedTokenValue = stringRedisTemplate.opsForValue().getAndDelete(hashedToken);
        if (hashedTokenValue == null) {
            throw new BusinessException(ErrorEnum.UNAUTHORIZED_REQUEST);
        }
        User user = userRepository.findByEmail(hashedTokenValue).orElseThrow(() ->
                new BusinessException(ErrorEnum.USER_NOT_FOUND,
                        "No user found for give Email ID: " + hashedTokenValue));
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        if (user.getOpaqueIdentifier() != null) {
            userIdentifierService.invalidateUserCache(user.getOpaqueIdentifier());
            log.debug("Cache invalidated for user after password reset: {}", user.getUserName());
        }
    }
}

