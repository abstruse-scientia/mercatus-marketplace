package com.scientia.mercatus.security;

import com.scientia.mercatus.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpaqueIdentifierCache {

    private final CacheManager cacheManager;
    private static final String CACHE_NAME = "opaque_identifiers";

    @Cacheable(value = CACHE_NAME, key = "#opaqueId", unless = "#result == null")
    public Optional<User> getUserFromCache(String opaqueId) {
        log.debug("Cache lookup for opaque identifier: {}", opaqueId);
        return Optional.empty();
    }

    @CachePut(value = CACHE_NAME, key = "#opaqueId")
    public Optional<User> cacheUserMapping(String opaqueId, User user) {
        log.debug("Caching user mapping for opaque identifier: {}", opaqueId);
        return Optional.of(user);
    }

    @CacheEvict(value = CACHE_NAME, key = "#opaqueId")
    public void invalidateCache(String opaqueId) {
        log.debug("Evicting cache entry for opaque identifier: {}", opaqueId);
    }

    @CacheEvict(value = CACHE_NAME, allEntries = true)
    public void clearAllCache() {
        log.debug("Clearing all opaque identifier cache entries");
    }
}

