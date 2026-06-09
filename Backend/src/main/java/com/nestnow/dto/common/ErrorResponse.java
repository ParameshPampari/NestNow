package com.nestnow.dto.common;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {

    private String message;

    private int status;

    private LocalDateTime timestamp;
}