package com.scientia.mercatus.controller;

import com.scientia.mercatus.dto.LoginRequestDto;
import com.scientia.mercatus.dto.LoginResponseDto;
import com.scientia.mercatus.dto.UserDto;
import com.scientia.mercatus.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    @PostMapping("/login")
    public ResponseEntity<?> apiLogin(@RequestBody LoginRequestDto loginRequestDto) {
        try{
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequestDto.username(), loginRequestDto.password())
            );
            List<String> authorities = authentication.getAuthorities().stream().
                    map(GrantedAuthority::getAuthority).toList();
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new LoginResponseDto(HttpStatus.OK.getReasonPhrase(), authorities, null));

        }catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }


    }
}
