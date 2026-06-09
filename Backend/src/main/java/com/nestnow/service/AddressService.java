package com.nestnow.service;

import com.nestnow.dto.address.AddressResponse;
import com.nestnow.dto.address.CreateAddressRequest;

import java.util.List;

public interface AddressService {

    AddressResponse createAddress(
            String email,
            CreateAddressRequest request
    );

    List<AddressResponse> getMyAddresses(String email);
}
