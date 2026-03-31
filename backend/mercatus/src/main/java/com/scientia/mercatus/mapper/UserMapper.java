package com.scientia.mercatus.mapper;

import com.scientia.mercatus.dto.UserDto;
import com.scientia.mercatus.entity.Role;
import com.scientia.mercatus.entity.User;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;


@Service
public class UserMapper {

    public UserDto toUserDto(User user) {
        if (user == null) {
            return null;
        }

        UserDto userDto = new UserDto();
        userDto.setUserId(user.getUserId());
        userDto.setUsername(user.getUserName());
        userDto.setEmail(user.getEmail());
        userDto.setRoles(user.getRoles().stream()
                .map(Role::getName)
                .map(Enum::toString)
                .collect(Collectors.joining(",")));

        return userDto;
    }
}

