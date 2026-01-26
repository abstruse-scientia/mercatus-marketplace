package com.scientia.mercatus.service.AddressService;

import com.scientia.mercatus.entity.AddressSnapshot;
import com.scientia.mercatus.entity.UserAddress;
import com.scientia.mercatus.mapper.AddressMapper;
import com.scientia.mercatus.repository.UserAddressRepository;
import com.scientia.mercatus.security.AuthContext;
import com.scientia.mercatus.service.IAddressService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.flyway.enabled=false"
})
public class AddressServiceDeleteAddressIT {

    @Autowired
    private UserAddressRepository userAddressRepository;

    @Autowired
    private AddressMapper addressMapper;

    @Autowired
    private IAddressService addressService;

    @MockitoBean
    private AuthContext authContext;

    @BeforeEach
    void setup() {
        when(authContext.getCurrentUserId()).thenReturn(1L);
    }

    private AddressSnapshot getAddressSnapshot() {
        AddressSnapshot addressSnapshot = new AddressSnapshot();
        addressSnapshot.setCity("Example");
        addressSnapshot.setCountry("USA");
        addressSnapshot.setState("Oregon");
        addressSnapshot.setArea("First-Circuit");
        addressSnapshot.setPincode("89268");
        addressSnapshot.setMobileNumber("2222222333333");
        addressSnapshot.setFullName("John Doe");
        addressSnapshot.setFlatHouse("88-H");
        return addressSnapshot;
    }
    private UserAddress createAddress(){
        UserAddress userAddress = new UserAddress();
        userAddress.setUserId(1L);
        userAddress.setActive(true);
        userAddress.setDefault(false);
        userAddress.setAddressSnapshot(getAddressSnapshot());
        userAddressRepository.save(userAddress);
        return userAddress;
    }

    @Test
    void shouldDeleteAddress_whenAddressIdIsGiven() {

        UserAddress savedUserAddress = createAddress();

        addressService.deleteAddress(savedUserAddress.getId());

        UserAddress updatedUserAddress = userAddressRepository.findById(savedUserAddress.getUserId())
                .orElseThrow();

        assertFalse(updatedUserAddress.isActive());


    }
}
