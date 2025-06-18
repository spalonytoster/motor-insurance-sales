package com.example.motorinsurancesales.shared.errors;

import lombok.Builder;

@Builder
public record ApiErrorDetails(
        String errorCode,
        String message,
        String details) {
}
