package com.scientia.mercatus.service;

import com.scientia.mercatus.dto.AddressRequestDto;
import com.scientia.mercatus.dto.UserAddressDto;



import java.util.List;

public interface IAddressService {

    UserAddressDto addAddress(AddressRequestDto addressRequestDto);

    List<UserAddressDto> getAddresses();

    void deleteAddress(Long addressId);

    UserAddressDto getDefaultAddress();
}
