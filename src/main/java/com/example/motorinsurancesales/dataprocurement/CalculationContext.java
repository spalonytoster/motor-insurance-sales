package com.example.motorinsurancesales.dataprocurement;

public record CalculationContext(
        // General info
        Object salesmanContext,
        Object customerSalesHistory, // possible to define churn rate?
        Object activePolicy, // if active policy exists, then we're in either renewal or after-sales
        Object customerPersonalInformation,
        Object customerClaimsHistory,
        Object apk,

        // MOTOR product specific info used in risk assessment - we got these from declarations via forms
        Object customerDrivingRecord, // traffic events, accidents, penalty points
        VehicleData vehicleDetails, // should we generalize to AssetDetails and move up??
        Object vehicleHistory,
        Object vehicleUsage
) {
}
