package com.scientia.mercatus.service.impl;

import com.scientia.mercatus.dto.AddressRequestDto;
import com.scientia.mercatus.dto.UserAddressDto;
import com.scientia.mercatus.entity.AddressSnapshot;
import com.scientia.mercatus.entity.User;
import com.scientia.mercatus.entity.UserAddress;
import com.scientia.mercatus.exception.AddressNotFoundException;
import com.scientia.mercatus.exception.UnauthorizedOperationException;
import com.scientia.mercatus.mapper.AddressMapper;
import com.scientia.mercatus.repository.UserAddressRepository;
import com.scientia.mercatus.repository.UserRepository;
import com.scientia.mercatus.service.IAddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements IAddressService {

    private final UserAddressRepository userAddressRepository;
    private final AddressMapper addressMapper;

    private Long getLoggedInUserId() {
       Authentication auth = SecurityContextHolder.getContext().getAuthentication();
       if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
           throw new UnauthorizedOperationException("User not allowed to access this resource");
       }
       if (!(auth.getPrincipal() instanceof User)) {
           throw new UnauthorizedOperationException("Invalid Principal Type");
       }
       User user = (User) auth.getPrincipal();
       return user.getUserId();
    }

    @Override
    @Transactional
    public UserAddressDto addAddress(AddressRequestDto addressRequestDto) {
        Long userId  = getLoggedInUserId();
        UserAddress userAddress = new UserAddress();
        userAddress.setUserId(userId);
        AddressSnapshot addressSnapshot = getAddressSnapshot(addressRequestDto);

        if (addressRequestDto.isDefault()) {
            userAddressRepository.clearDefaultUser(userId);
            userAddress.setDefault(true);
        }
        userAddress.setAddressSnapshot(addressSnapshot);
        userAddress.setActive(true);
        userAddressRepository.save(userAddress);
        return addressMapper.toUserAddressDto(userAddress);
    }



    @Override
    @Transactional
    public void deleteAddress(Long addressId) {
        Long userId = getLoggedInUserId();

        UserAddress address = userAddressRepository.findByIdAndUserIdAndIsActiveTrue(addressId, userId)
                .orElseThrow(()-> new AddressNotFoundException("Given address does not exist"));

        address.setActive(false);
        if (address.isDefault()) {
            address.setDefault(false);
        }
    }

    @Override
    public List<UserAddressDto> getAddresses() {
        Long userId = getLoggedInUserId();
        List <UserAddress> userAddresses = userAddressRepository.findByUserIdAndIsActiveTrue(userId);
        return userAddresses.stream().map(
                addressMapper::toUserAddressDto
        ).toList();

    }

    @Override
    public UserAddressDto getDefaultAddress() {
        Long userId = getLoggedInUserId();
        UserAddress userAddress =  userAddressRepository.findByUserIdAndIsDefaultTrueAndIsActiveTrue(userId).orElseThrow(() ->
                new AddressNotFoundException("No default address exists"));
        return addressMapper.toUserAddressDto(userAddress);
    }


    //Helper functions


    private static AddressSnapshot getAddressSnapshot(AddressRequestDto addressRequestDto) {
        AddressSnapshot addressSnapshot = new AddressSnapshot();
        addressSnapshot.setFullName(addressRequestDto.getFullName());
        addressSnapshot.setMobileNumber(addressRequestDto.getMobileNumber());
        addressSnapshot.setFlatHouse(addressRequestDto.getFlatHouse());
        addressSnapshot.setArea(addressRequestDto.getArea());
        addressSnapshot.setLandmark(addressRequestDto.getLandmark());
        addressSnapshot.setPincode(addressRequestDto.getPincode());
        addressSnapshot.setState(addressRequestDto.getState());
        addressSnapshot.setCity(addressRequestDto.getCity());
        addressSnapshot.setCountry(addressRequestDto.getCountry());
        return addressSnapshot;
    }
}
