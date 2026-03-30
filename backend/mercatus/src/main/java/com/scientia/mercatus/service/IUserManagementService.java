package com.scientia.mercatus.service;

import com.scientia.mercatus.entity.Role;
import com.scientia.mercatus.entity.User;

import java.util.Set;

public interface IUserManagementService {
    
    void updateUserRoles(Long userId, Set<Role> newRoles);
    
    void deactivateUser(Long userId);
    
    void deleteUser(Long userId);
    
    void updateUserPassword(Long userId, String newPassword);
}

