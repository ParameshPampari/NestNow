package com.nestnow.controller;

import com.nestnow.dto.marketplace.CreateServiceRequest;
import com.nestnow.dto.marketplace.ServiceResponse;
import com.nestnow.service.MarketplaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class MarketplaceController {

    private final MarketplaceService marketplaceService;

    @PostMapping("/create")
    public ServiceResponse createService(
            @RequestBody CreateServiceRequest request
    ) {

        return marketplaceService.createService(request);
    }

    @GetMapping
    public List<ServiceResponse> getAllServices() {

        return marketplaceService.getAllServices();
    }

    @GetMapping("/search")
    public List<ServiceResponse> searchServices(
            @RequestParam(required = false) String q
    ) {

        return marketplaceService.searchServices(q);
    }

    @GetMapping("/category/{categoryId}")
    public List<ServiceResponse> getServicesByCategory(
            @PathVariable Long categoryId
    ) {

        return marketplaceService
                .getServicesByCategory(categoryId);
    }
}
