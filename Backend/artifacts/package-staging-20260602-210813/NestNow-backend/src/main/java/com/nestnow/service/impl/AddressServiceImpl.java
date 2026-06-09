package com.nestnow.service.impl;

import com.nestnow.dto.address.AddressResponse;
import com.nestnow.dto.address.CreateAddressRequest;
import com.nestnow.entity.Address;
import com.nestnow.entity.User;
import com.nestnow.exception.ResourceNotFoundException;
import com.nestnow.repository.AddressRepository;
import com.nestnow.repository.UserRepository;
import com.nestnow.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    @Override
    public AddressResponse createAddress(
            String email,
            CreateAddressRequest request
    ) {

        User user = findUser(email);

        Address address = Address.builder()
                .label(request.getLabel())
                .line1(request.getLine1())
                .line2(request.getLine2())
                .city(request.getCity())
                .state(request.getState())
                .pincode(request.getPincode())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .user(user)
                .build();

        addressRepository.save(address);

        return mapToResponse(address);
    }

    @Override
    public List<AddressResponse> getMyAddresses(String email) {

        User user = findUser(email);

        return addressRepository.findByUser(user)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private User findUser(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found")
                );
    }

    private AddressResponse mapToResponse(Address address) {

        return AddressResponse.builder()
                .id(address.getId())
                .label(address.getLabel())
                .line1(address.getLine1())
                .line2(address.getLine2())
                .city(address.getCity())
                .state(address.getState())
                .pincode(address.getPincode())
                .latitude(address.getLatitude())
                .longitude(address.getLongitude())
                .build();
    }
}
