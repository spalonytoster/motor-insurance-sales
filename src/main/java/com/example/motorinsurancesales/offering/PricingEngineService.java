package com.example.motorinsurancesales.offering;

import com.example.motorinsurancesales.dataprocurement.CalculationContext;
import com.example.motorinsurancesales.underwritingcases.UnderwritingCaseException;
import lombok.SneakyThrows;

import java.util.List;
import java.util.Optional;

class PricingEngineService {

//    @SneakyThrows
    Optional<List<InsuranceSalesProfile>> calculateOptionsAndPricing(CalculationContext calculationContext) {
        try {
            return Optional.ofNullable(TiaClient.calculate(calculationContext));
        } catch (UnderwritingCaseException e) {
            throw new RuntimeException(e);
        }
    }

    private class TiaClient {
        public static List<InsuranceSalesProfile> calculate(CalculationContext calculationContext) throws UnderwritingCaseException {
            // whoopsie, case has been filed
            throw new UnderwritingCaseException(1L, "something is no yes");
        }
    }
}
