package com.example.motorinsurancesales.dataprocurement.enrichment;

import com.example.motorinsurancesales.dataprocurement.VehicleData;

// transform VIN to VehicleData
class VINTransformer implements EnrichmentPipe<VIN, VehicleData> {
    @Override
    public TransformationResult<VehicleData> transform(VIN input) {
        // implement calls to external services
        return null;
    }
}

record VIN(String vin) {}
