package com.scientia.mercatus.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserAddressDto {

    private Long addressId;
    private boolean isDefault;
    private String fullName;
    private String mobileNumber;
    private String flatHouse;
    private String area;
    private String landmark;
    private String pincode;
    private String city;
    private String state;
    private String country;

}
