package com.scientia.mercatus.controller;



import com.scientia.mercatus.dto.Auth.ResetPasswordContextDto;
import com.scientia.mercatus.security.SpringSecurityAuthContext;

import com.scientia.mercatus.service.IUserManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final IUserManagementService userManagementService;
    private final SpringSecurityAuthContext springSecurityAuthContext;


    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestParam String newPassword) {
        Long id = springSecurityAuthContext.getCurrentUserId();
        userManagementService.updateUserPassword(id, newPassword);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        userManagementService.forgotUserPassword(email);
        return ResponseEntity.status(HttpStatus.OK).build();

    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordContextDto resetPasswordContext) {
        userManagementService.resetPassword(resetPasswordContext.accessToken(),
                resetPasswordContext.newPassword());
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
