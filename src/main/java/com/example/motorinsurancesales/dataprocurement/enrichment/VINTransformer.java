package com.example.motorinsurancesales.dataprocurement.enrichment;

import com.example.motorinsurancesales.dataprocurement.VehicleData;
import com.example.motorinsurancesales.dataprocurement.forms.VIN;

// transform VIN to VehicleData
class VINTransformer implements TransformationPipe<VIN, VehicleData> {
    @Override
    public TransformationResult<VehicleData> transform(VIN input) {
        // implement calls to external services
        return null;
    }
}

