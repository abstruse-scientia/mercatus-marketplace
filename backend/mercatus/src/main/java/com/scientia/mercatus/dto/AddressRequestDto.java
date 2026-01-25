package com.scientia.mercatus.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressRequestDto {

    @NotBlank
    private String fullName;

    @NotBlank
    private String mobileNumber;

    @NotBlank
    private String flatHouse;

    @NotBlank
    private String area;

    private String landmark;

    @NotBlank
    private String pincode;

    @NotBlank
    private String state;

    @NotBlank
    private String city;

    @NotBlank
    private String country;

    private boolean isDefault;
}
