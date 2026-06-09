package com.nestnow.controller;

import com.nestnow.dto.address.AddressResponse;
import com.nestnow.dto.address.CreateAddressRequest;
import com.nestnow.service.AddressService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @PostMapping
    public AddressResponse createAddress(
            @Parameter(hidden = true)
            Authentication authentication,
            @RequestBody CreateAddressRequest request
    ) {

        return addressService.createAddress(
                authentication.getName(),
                request
        );
    }

    @GetMapping
    public List<AddressResponse> getMyAddresses(
            @Parameter(hidden = true)
            Authentication authentication
    ) {

        return addressService.getMyAddresses(
                authentication.getName()
        );
    }
}
