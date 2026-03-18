package com.scientia.mercatus.service.impl;

import com.scientia.mercatus.entity.User;
import com.scientia.mercatus.exception.BusinessException;
import com.scientia.mercatus.exception.ErrorEnum;
import com.scientia.mercatus.repository.UserRepository;
import com.scientia.mercatus.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IUserServiceImpl implements IUserService {

    private final UserRepository userRepository;

    @Override
    public User getUser(Long userId) {
        return  userRepository.findById(userId).orElseThrow(
                ()-> new BusinessException(ErrorEnum.NO_LOGGED_IN_USER_FOUND)
        );
    }
}
