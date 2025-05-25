package com.example.motorinsurancesales.dataprocurement;

interface DomainEvent {
//    record CustomerCreated() implements DomainEvent {}
    record CalculationDataChanged(Object data) implements DomainEvent {}
}
