package com.corporation_dev.user_microservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponseDto {
    private boolean success;
    private String message;
    private String errorCode;
    private String timestamp;
}
