package com.example.motorinsurancesales.dataprocurement;

// dzień dobry, zgłaszam to do prokuratury XD

// for now i have no idea how to name it better
// it's a sub-domain that will be rich in events creation since every submit from frontend might potentially trigger rules
// we shall listen to events from Forms

import com.example.motorinsurancesales.dataprocurement.DomainEvent.CalculationDataChanged;
import com.example.motorinsurancesales.dataprocurement.enrichment.TransformationPipe;
import org.springframework.context.ApplicationEventPublisher;

import java.util.HashMap;
import java.util.Map;

import static com.example.motorinsurancesales.dataprocurement.enrichment.TransformationPipe.*;

// we could call it CalculationContextAccumulator
class DataProcurementManager {

    ApplicationEventPublisher publisher;
    CalculationContext calculationContext;

    private Map<Class<Input>, TransformationPipe<Input, Output>> pipes = new HashMap<>();

    void handleInputData(Input inputData) {
        var transformer = pipes.get(inputData.getClass());
        var result = transformer.transform(inputData);
        publisher.publishEvent(new CalculationDataChanged(result));
    }
}
