package com.example.motorinsurancesales.offering;

import java.util.List;

record InsuranceCoverage(
        List<CoverageOption> coverageOptions
) {

    Long calculatePremium() {
        // sum premiums from all coverage options
        return null;
    }
}
