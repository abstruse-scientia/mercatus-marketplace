package com.scientia.mercatus.service;

import com.scientia.mercatus.dto.Product.Admin.AdminUserSummaryDto;
import com.scientia.mercatus.entity.Role;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;

public interface IUserManagementService {
    
    void updateUserRoles(Long userId, Set<Role> newRoles);
    
    void deactivateUser(Long userId);

    void reactivateUser(Long userId);
    
    void deleteUser(Long userId);
    
    void updateUserPassword(Long userId, String newPassword);

    void forgotUserPassword(String email);

    void resetPassword(String accessToken, String newPassword);

    Page<AdminUserSummaryDto> listAllUsers(Pageable pageable);

    void validateAction(Long userId, String message);
}
