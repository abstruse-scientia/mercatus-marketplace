package com.scientia.mercatus.factory;

import com.scientia.mercatus.entity.User;

import java.util.UUID;

public class UserFactory {

    public static User create() {
        return create(UUID.randomUUID() + "@test.com");
    }

    public static User create(String email) {
        User user = new User();
        user.setUserName("John Doe");
        user.setEmail(email);
        user.setPasswordHash("password-hash");
        return user;
    }
}
