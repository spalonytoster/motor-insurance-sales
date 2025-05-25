package com.example.motorinsurancesales.dataprocurement.forms;

import com.example.motorinsurancesales.dataprocurement.enrichment.TransformationPipe;

import static com.example.motorinsurancesales.dataprocurement.enrichment.TransformationPipe.*;

public record VIN(String vin) implements Input {}
