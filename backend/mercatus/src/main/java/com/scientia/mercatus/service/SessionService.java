package com.scientia.mercatus.service;

public interface SessionService {

    String createSession();
    boolean validateSession(String sessionId);
    void revokeSession(String sessionId);
}
