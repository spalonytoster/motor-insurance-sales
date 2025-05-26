package com.example.motorinsurancesales.checkout;

interface DomainEvent {
    record PaymentSuccessful() implements DomainEvent {}
    record PaymentFailed() implements DomainEvent {}
    record DocumentsPrinted() implements DomainEvent {}
    record CustomerChoseRemoteOffer() implements DomainEvent {}
}