package com.example.motorinsurancesales.offering;

import com.example.motorinsurancesales.dataprocurement.CalculationContext;

interface PricingEngineService {
    void calculateOptionsAndPricing(CalculationContext calculationContext);
}
