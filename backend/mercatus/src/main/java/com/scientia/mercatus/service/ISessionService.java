package com.scientia.mercatus.service;

public interface ISessionService {

    String createSession();
    boolean validateSession(String sessionId);
    void revokeSession(String sessionId);
}
