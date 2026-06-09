package com.nestnow.service.impl;

import com.nestnow.dto.marketplace.CreateServiceRequest;
import com.nestnow.dto.marketplace.ServiceResponse;
import com.nestnow.entity.Category;
import com.nestnow.entity.ServiceEntity;
import com.nestnow.exception.ResourceNotFoundException;
import com.nestnow.repository.CategoryRepository;
import com.nestnow.repository.ServiceRepository;
import com.nestnow.service.MarketplaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MarketplaceServiceImpl
        implements MarketplaceService {

    private final ServiceRepository serviceRepository;

    private final CategoryRepository categoryRepository;

    @Override
    public ServiceResponse createService(
            CreateServiceRequest request
    ) {

        Category category = categoryRepository
                .findById(request.getCategoryId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category not found"
                        )
                );

        ServiceEntity service = ServiceEntity.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .price(request.getPrice())
                .duration(request.getDuration())
                .category(category)
                .build();

        serviceRepository.save(service);

        return mapToResponse(service);
    }

    @Override
    public List<ServiceResponse> getAllServices() {

        return serviceRepository.findByActiveTrue()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ServiceResponse> getServicesByCategory(
            Long categoryId
    ) {

        Category category = categoryRepository
                .findById(categoryId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category not found"
                        )
                );

        return serviceRepository.findByActiveTrueAndCategory(category)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ServiceResponse> searchServices(String query) {

        if (query == null || query.isBlank()) {
            return getAllServices();
        }

        return serviceRepository
                .findByActiveTrueAndTitleContainingIgnoreCase(query)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private ServiceResponse mapToResponse(
            ServiceEntity service
    ) {

        return ServiceResponse.builder()
                .id(service.getId())
                .title(service.getTitle())
                .description(service.getDescription())
                .price(service.getPrice())
                .duration(service.getDuration())
                .categoryName(service.getCategory().getName())
                .categoryId(service.getCategory().getId())
                .active(service.getActive())
                .build();
    }
}
