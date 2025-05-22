package com.example.motorinsurancesales.offering;

sealed interface DomainEvents {
    record SalesResumed() implements DomainEvents {}
    record InitialCalculationContextChanged() implements DomainEvents {}
    record AvailabilityCalculated() implements DomainEvents {}
    record DiscountApplied() implements DomainEvents {}
    record InsuranceTraitSelected() {} // this means that availability has been limited. does it deserve to be a domain event? or is it just a state change? if we want to do event sourcing, then it should ---triggers---> AvailabilityChanged
    record OfferAccepted() implements DomainEvents {} // going forward in the conversion funnel
    record PolicyConcluded() implements DomainEvents {}
    record OfferAbandoned() implements DomainEvents {} // equivalent with dropped session
    record OfferExpired() implements DomainEvents {}
}
