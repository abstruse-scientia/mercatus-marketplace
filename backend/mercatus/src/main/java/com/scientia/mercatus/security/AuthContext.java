package com.scientia.mercatus.security;


public interface AuthContext {
    Long getCurrentUserId();
    Long getCurrentUserIdOrNull();
}
