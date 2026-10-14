package com.scientia.mercatus.controller;

import com.scientia.mercatus.dto.Product.Admin.AdminUserSummaryDto;
import com.scientia.mercatus.entity.Role;
import com.scientia.mercatus.service.IUserManagementService;
import com.scientia.mercatus.util.PageableUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("api/v1/admin/user")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {


    private final  IUserManagementService userManagementService;

    @PostMapping("/all")
    public ResponseEntity<Page<AdminUserSummaryDto>> listUsers(Pageable pageable){
        Pageable pages = PageableUtil.getSafePageable(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(userManagementService.listAllUsers(pages));
    }


    @PostMapping("/{userId}/deactivate")
    public ResponseEntity<Void> deactivateUser(@PathVariable Long userId){
        userManagementService.validateAction(userId, "You cannot deactivate your own ID.");
        userManagementService.deactivateUser(userId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    public ResponseEntity<Void> updateUserRoles(@PathVariable Long userId, @RequestBody Set<Role> roles) {
        userManagementService.validateAction(userId, "You cannot modify your own roles.");
        userManagementService.updateUserRoles(userId, roles);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userManagementService.validateAction(userId, "You cannot delete your own admin account.");
        userManagementService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
