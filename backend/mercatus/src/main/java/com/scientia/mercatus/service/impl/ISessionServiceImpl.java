package com.scientia.mercatus.service.impl;

import com.scientia.mercatus.service.ISessionService;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ISessionServiceImpl implements ISessionService {

    private final Set<String> revokeSessionIds =  ConcurrentHashMap.newKeySet();
    @Override
    public String createSession() {
        return UUID.randomUUID().toString();
    }

    @Override
    public void revokeSession(String sessionId) {
        if (sessionId != null) {
            revokeSessionIds.add(sessionId);
        }
    }
    @Override
    public boolean validateSession(String sessionId) {
        if (sessionId == null || sessionId.isBlank() ){
            return false;
        }
        return !revokeSessionIds.contains(sessionId);
    }
}
