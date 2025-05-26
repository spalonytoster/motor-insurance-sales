package com.example.motorinsurancesales.dataprocurement.enrichment;

import com.example.motorinsurancesales.dataprocurement.enrichment.TransformationPipe.Output;

import static com.example.motorinsurancesales.dataprocurement.enrichment.TransformationPipe.*;

class CustomerHistoryByPesel implements TransformationPipe<PESEL, CustomerHistory> {
    @Override
    public TransformationResult<CustomerHistory> transform(PESEL input) {
        return TransformationResult.ok(new CustomerHistory("tiru diru"));
    }
}

record PESEL(String value) implements Input {}

record CustomerHistory(String value) implements Output {}