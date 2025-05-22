package com.example.motorinsurancesales.offering;

sealed interface DomainEvents {
    record OfferAccepted() implements DomainEvents {}
}
