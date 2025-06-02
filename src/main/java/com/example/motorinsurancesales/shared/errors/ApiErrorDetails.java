package com.example.motorinsurancesales.shared.errors;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public record ApiErrorDetails(
        String errorCode,
        String message,
        String details) {
}
