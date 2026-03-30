package com.scientia.mercatus.security;

import com.scientia.mercatus.entity.User;
import com.scientia.mercatus.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserIdentifierService {

    private final UserRepository userRepository;
    private final OpaqueIdentifierCache opaqueIdentifierCache;

    @Transactional
    public String getOrCreateOpaqueIdentifier(User user) {
        if (user.getOpaqueIdentifier() != null && !user.getOpaqueIdentifier().isEmpty()) {
            return user.getOpaqueIdentifier();
        }

        String opaqueId = generateOpaqueIdentifier();
        user.setOpaqueIdentifier(opaqueId);
        userRepository.save(user);
        opaqueIdentifierCache.cacheUserMapping(opaqueId, user);

        log.debug("Generated new opaque identifier for user: {}", user.getUserId());
        return opaqueId;
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserByOpaqueIdentifier(String opaqueId) {
        Optional<User> cachedUser = opaqueIdentifierCache.getUserFromCache(opaqueId);
        if (cachedUser.isPresent()) {
            log.debug("Cache hit for opaque identifier lookup");
            return cachedUser;
        }

        log.debug("Cache miss for opaque identifier, querying database");
        Optional<User> user = userRepository.findByOpaqueIdentifier(opaqueId);
        user.ifPresent(value -> opaqueIdentifierCache.cacheUserMapping(opaqueId, value));
        return user;
    }

    private String generateOpaqueIdentifier() {
        return UUID.randomUUID().toString();
    }

    @Transactional
    public void invalidateUserCache(String opaqueId) {
        opaqueIdentifierCache.invalidateCache(opaqueId);
        log.debug("Invalidated cache for opaque identifier: {}", opaqueId);
    }
}

