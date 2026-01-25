package com.scientia.mercatus.controller;

import com.scientia.mercatus.dto.AddressRequestDto;
import com.scientia.mercatus.dto.UserAddressDto;
import com.scientia.mercatus.service.IAddressService;
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


    @PostMapping("/add")
    public ResponseEntity<UserAddressDto> addAddress(@RequestBody AddressRequestDto addressDto) {
        UserAddressDto userAddressDto = addressService.addAddress(addressDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(userAddressDto);
    }

    @GetMapping("/get")
    public ResponseEntity<List<UserAddressDto>> getAddress() {
        List<UserAddressDto> addresses = addressService.getAddresses();
        return ResponseEntity.status(HttpStatus.OK).body(addresses);
    }








}
