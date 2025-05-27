package com.example.motorinsurancesales.common.errors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiErrorDetails> handleApplicationError(Exception e) {
        log.error(e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiErrorDetails.builder()
                        .message("Unexpected error occured at runtime.")
                        .details("X-Request-Tracking-Id=" + "TODO: access request headers here - probably easiest solution is to use included MDC")
                        .build());
    }
}
