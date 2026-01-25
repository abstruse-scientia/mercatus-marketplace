package com.scientia.mercatus.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class AddressSnapshot {

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "mobile_number", nullable = false, length = 15)
    private String mobileNumber;


    @Column(name = "flat_house", nullable = false, length = 255)
    private String flatHouse;

    @Column(nullable = false, length = 255)
    private String area;

    @Column(length = 255)
    private String landmark;

    @Column(nullable = false, length = 10)
    private String pincode;

    @Column(nullable = false, length = 50)
    private String city;

    @Column(nullable = false, length = 50)
    private String state;


    @Column(nullable = false, length = 50)
    private String country;

}
