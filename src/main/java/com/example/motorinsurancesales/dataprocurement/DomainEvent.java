package com.example.motorinsurancesales.dataprocurement;

interface DomainEvent {
    // this would be good if we wouldn't have to respond in REST controllers (but we need to)
    // because of this, connection between forms and enrichment should be synchronous
    record CalculationDataChanged(Object data) implements DomainEvent {}
}
