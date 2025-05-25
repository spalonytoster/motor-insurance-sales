package com.example.motorinsurancesales.dataprocurement.forms.vehicledata;

import com.example.motorinsurancesales.dataprocurement.VehicleData;
import com.example.motorinsurancesales.dataprocurement.forms.VIN;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/forms/vehicle-data")
class VehicleDataController {



    @PostMapping("/")
    public ResponseEntity<VehicleData> submitVin(@Validated VIN vin) {
        return null;
    }
}
