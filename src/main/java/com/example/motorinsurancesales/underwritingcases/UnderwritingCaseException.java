package com.example.motorinsurancesales.underwritingcases;

import lombok.Getter;

import static java.lang.String.format;

@Getter
public class UnderwritingCaseException extends Exception {

    private final Long caseId;
    private final String caseDetails;

    public UnderwritingCaseException(Long caseId, String caseDetails) {
        super(format("Case with id: %d has been filed. Case details: %s", caseId, caseDetails));
        this.caseId = caseId;
        this.caseDetails = caseDetails;
    }
}