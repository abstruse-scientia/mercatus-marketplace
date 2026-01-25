package com.scientia.mercatus.mapper;

import com.scientia.mercatus.dto.UserAddressDto;
import com.scientia.mercatus.entity.UserAddress;
import org.springframework.stereotype.Service;

@Service
public class AddressMapper {

    public UserAddressDto toUserAddressDto(UserAddress userAddress) {
        UserAddressDto userAddressDto = new UserAddressDto();
        userAddressDto.setAddressId(userAddress.getId());
        userAddressDto.setDefault(userAddress.isDefault());
        userAddressDto.setFullName(userAddress.getAddressSnapshot().getFullName());
        userAddressDto.setMobileNumber(userAddress.getAddressSnapshot().getMobileNumber());
        userAddressDto.setFlatHouse(userAddress.getAddressSnapshot().getFlatHouse());
        userAddressDto.setArea(userAddress.getAddressSnapshot().getArea());
        userAddressDto.setLandmark(userAddress.getAddressSnapshot().getLandmark());
        userAddressDto.setPincode(userAddress.getAddressSnapshot().getPincode());
        userAddressDto.setCity(userAddress.getAddressSnapshot().getCity());
        userAddressDto.setState(userAddress.getAddressSnapshot().getState());
        userAddressDto.setCountry(userAddress.getAddressSnapshot().getCountry());

        return userAddressDto;
    }
}
