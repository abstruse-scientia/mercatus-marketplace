package com.scientia.mercatus.dto.Product.Admin;

import com.scientia.mercatus.entity.Role;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Getter
@Setter
public class AdminUserSummaryDto {

    private String userName;

    @Setter(AccessLevel.NONE)
    private String email;

    public void setEmail(String email) {
        if (email == null) {
            throw new RuntimeException("Email cannot be null");
        }
        int atIndex = email.indexOf('@');
        if (atIndex == -1) {
            throw new RuntimeException("Invalid email");
        }

        // Uses IntStream of Stream API to create a number of "*";
        String repeatedString = IntStream.range(0, atIndex - 2)
                .mapToObj(i -> "*").collect(Collectors.joining());
        // Leave the first two character of email as it is and add remaining "*" for masking;
        String maskedPart = email.substring(0, atIndex - repeatedString.length()) + repeatedString;

        this.email = maskedPart + email.substring(atIndex);
    }

    Set<Role> roles;

    boolean isActive;

}
