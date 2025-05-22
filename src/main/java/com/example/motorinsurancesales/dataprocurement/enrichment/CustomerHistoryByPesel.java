package com.example.motorinsurancesales.dataprocurement.enrichment;

class CustomerHistoryByPesel implements EnrichmentPipe<PESEL, CustomerHistory> {
    @Override
    public TransformationResult<CustomerHistory> transform(PESEL input) {
        return TransformationResult.ok(new CustomerHistory("tiru diru"));
    }
}

record PESEL(String value) {}

record CustomerHistory(String value) {}