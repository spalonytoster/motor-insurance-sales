package com.example.motorinsurancesales.shared.errors;

import lombok.Builder;
import lombok.Getter;

@Builder
public record ApiErrorDetails(
        String errorCode,
        String message,
        String details) {
}
