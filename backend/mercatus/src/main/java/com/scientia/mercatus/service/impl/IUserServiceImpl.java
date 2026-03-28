package com.scientia.mercatus.service.impl;

import com.scientia.mercatus.entity.Role;
import com.scientia.mercatus.entity.RoleName;
import com.scientia.mercatus.entity.User;
import com.scientia.mercatus.exception.BusinessException;
import com.scientia.mercatus.exception.ErrorEnum;
import com.scientia.mercatus.repository.RoleRepository;
import com.scientia.mercatus.repository.UserRepository;
import com.scientia.mercatus.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class IUserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User getUser(Long userId) {
        return  userRepository.findById(userId).orElseThrow(
                ()-> new BusinessException(ErrorEnum.NO_LOGGED_IN_USER_FOUND)
        );
    }

    @Override
    @Transactional
    public User registerUser(String email, String password, String userName) throws BusinessException {
        
        // Step 1: Double-check email uniqueness (thread safety)
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException(ErrorEnum.USER_EMAIL_ALREADY_EXISTS);
        }

        try {
            // Step 2: Create User entity
            User user = new User();
            user.setEmail(email);
            user.setUserName(userName);
            user.setPasswordHash(passwordEncoder.encode(password));  // Hash password with BCrypt

            // Step 3: Get ROLE_USER from database
            Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                    .orElseThrow(() -> new BusinessException(ErrorEnum.ROLE_NOT_FOUND));

            // Step 4: Assign role to user
            user.getRoles().add(userRole);

            // Step 5: Save user to database and return
            return userRepository.save(user);

        } catch (BusinessException ex) {
            throw ex;  // Re-throw business exceptions
        } catch (Exception ex) {
            throw new BusinessException(ErrorEnum.INTERNAL_ERROR, ex.getMessage());
        }
    }

    @Override
    @Transactional
    public User registerAdmin(String email, String password, String userName) throws BusinessException {
        
        // Step 1: Double-check email uniqueness (thread safety)
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException(ErrorEnum.USER_EMAIL_ALREADY_EXISTS);
        }

        try {
            // Step 2: Create User entity
            User user = new User();
            user.setEmail(email);
            user.setUserName(userName);
            user.setPasswordHash(passwordEncoder.encode(password));  // Hash password with BCrypt

            // Step 3: Get ROLE_USER from database (admins are also users)
            Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                    .orElseThrow(() -> new BusinessException(ErrorEnum.ROLE_NOT_FOUND));

            // Step 4: Get ROLE_ADMIN from database
            Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN)
                    .orElseThrow(() -> new BusinessException(ErrorEnum.ROLE_NOT_FOUND));

            // Step 5: Assign both roles to admin user ( approach: admins have user + admin roles)
            user.getRoles().add(userRole);
            user.getRoles().add(adminRole);

            // Step 6: Save user to database and return
            return userRepository.save(user);

        } catch (BusinessException ex) {
            throw ex;  // Re-throw business exceptions
        } catch (Exception ex) {
            throw new BusinessException(ErrorEnum.INTERNAL_ERROR, ex.getMessage());
        }
    }
}
