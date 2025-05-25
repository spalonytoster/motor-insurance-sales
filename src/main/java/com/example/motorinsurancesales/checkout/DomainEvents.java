package com.example.motorinsurancesales.checkout;

interface DomainEvents {
    record PaymentSuccessful() implements DomainEvents {}
    record PaymentFailed() implements DomainEvents {}
    record DocumentsPrinted() implements DomainEvents {}
    record CustomerChoseRemoteOffer() implements DomainEvents {}
}