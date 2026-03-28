package com.scientia.mercatus.service;

import com.scientia.mercatus.entity.User;
import com.scientia.mercatus.exception.BusinessException;

public interface IUserService {
    User getUser(Long userId);

    User registerUser(String email, String password, String userName) throws BusinessException;

    User registerAdmin(String email, String password, String userName) throws BusinessException;
}
