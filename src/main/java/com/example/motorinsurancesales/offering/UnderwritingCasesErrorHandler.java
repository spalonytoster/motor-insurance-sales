package com.example.motorinsurancesales.offering;

import com.example.motorinsurancesales.common.errors.ApiErrorDetails;
import com.example.motorinsurancesales.underwritingcases.UnderwritingCaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
class UnderwritingCasesErrorHandler {

    @ExceptionHandler(UnderwritingCaseException.class)
    ResponseEntity<ApiErrorDetails> handleApplicationError(UnderwritingCaseException e) {
        log.error("Filed caseId: {}, caseDetails: {}", e.getCaseId(), e.getCaseDetails());
        return ResponseEntity
                .status(HttpStatus.I_AM_A_TEAPOT)
                .body(ApiErrorDetails.builder()
                        .errorCode("CASES-1")
                        .message(e.getMessage())
                        .build());
    }
}
