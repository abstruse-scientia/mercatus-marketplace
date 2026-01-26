package com.scientia.mercatus.controller;

import com.scientia.mercatus.dto.AddressRequestDto;
import com.scientia.mercatus.dto.UserAddressDto;
import com.scientia.mercatus.service.IAddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/address")
@RequiredArgsConstructor
public class AddressController {

    private final IAddressService addressService;


    @PostMapping
    public ResponseEntity<UserAddressDto> addAddress(@Valid @RequestBody AddressRequestDto addressDto) {
        UserAddressDto userAddressDto = addressService.addAddress(addressDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(userAddressDto);
    }

    @GetMapping
    public ResponseEntity<List<UserAddressDto>> getAddress() {
        List<UserAddressDto> addresses = addressService.getAddresses();
        return ResponseEntity.status(HttpStatus.OK).body(addresses);
    }

    @GetMapping("/default")
    public ResponseEntity<UserAddressDto> getDefaultAddress() {
        UserAddressDto userAddressDto = addressService.getDefaultAddress();
        return ResponseEntity.status(HttpStatus.OK).body(userAddressDto);
    }


    @DeleteMapping("/{addressId}")
    public ResponseEntity<?> deleteAddress(@PathVariable Long addressId) {
        addressService.deleteAddress(addressId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{addressId}")
    public ResponseEntity<UserAddressDto> updateAddress(@Valid @RequestBody AddressRequestDto addressDto,
                                                        @PathVariable Long addressId) {
        return ResponseEntity.ok(
                addressService.updateAddress(addressId, addressDto)
        );
    }











}
