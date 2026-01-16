package com.scientia.mercatus.service;

import com.scientia.mercatus.dto.CartContextDto;

public interface SessionService {

    String createSession();
    boolean validateSession(String sessionId);
    void revokeSession(String sessionId);
}
