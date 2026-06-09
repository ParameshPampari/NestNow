package com.nestnow.service;

import com.nestnow.dto.marketplace.CreateServiceRequest;
import com.nestnow.dto.marketplace.ServiceResponse;

import java.util.List;

public interface MarketplaceService {

    ServiceResponse createService(
            CreateServiceRequest request
    );

    List<ServiceResponse> getAllServices();

    List<ServiceResponse> getServicesByCategory(
            Long categoryId
    );

    List<ServiceResponse> searchServices(String query);
}
