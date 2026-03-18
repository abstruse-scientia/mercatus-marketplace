package com.scientia.mercatus.service;

import com.scientia.mercatus.dto.AddressRequestDto;
import com.scientia.mercatus.dto.UserAddressDto;
import com.scientia.mercatus.entity.AddressSnapshot;


import java.util.List;

public interface IAddressService {

    UserAddressDto addAddress(AddressRequestDto addressRequestDto);

    List<UserAddressDto> getAddresses();

    void deleteAddress(Long addressId);

    UserAddressDto getDefaultAddress();

    UserAddressDto updateAddress(Long addressId, AddressRequestDto addressDto);

    AddressSnapshot getAddressSnapshot(Long userId, Long addressId);
}
