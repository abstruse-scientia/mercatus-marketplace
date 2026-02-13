package com.scientia.mercatus.security;

import com.scientia.mercatus.entity.UserAddress;

public interface AuthContext {
    Long getCurrentUserId();
    Long getCurrentUserIdOrNull();
}
