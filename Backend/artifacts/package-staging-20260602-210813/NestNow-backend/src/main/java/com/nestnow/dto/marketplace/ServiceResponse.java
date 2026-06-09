package com.nestnow.dto.marketplace;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServiceResponse {

    private Long id;

    private String title;

    private String description;

    private Double price;

    private Integer duration;

    private String categoryName;

    private Boolean active;

    private Long categoryId;
}
