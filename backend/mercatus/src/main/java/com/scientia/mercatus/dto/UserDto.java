package com.scientia.mercatus.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserDto {
    private long userId;
    private String username;
    private String email;
    private String password;
    private String roles;

}
