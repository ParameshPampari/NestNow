package com.nestnow.dto.marketplace;

import lombok.Data;

@Data
public class CreateServiceRequest {

    private String title;

    private String description;

    private Double price;

    private Integer duration;

    private Long categoryId;
}