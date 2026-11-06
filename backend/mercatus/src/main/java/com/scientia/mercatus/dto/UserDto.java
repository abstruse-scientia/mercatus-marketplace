package com.scientia.mercatus.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class UserDto {
    private long userId;
    private String username;
    private String email;
    private String password;
    private List<String> roles;

}
