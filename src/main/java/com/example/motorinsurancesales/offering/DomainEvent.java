package com.example.motorinsurancesales.offering;

sealed interface DomainEvent {
    record SalesResumed() implements DomainEvent {}
    record InitialCalculationContextChanged() implements DomainEvent {} // might not be needed. instead typical calculation event will be emitted
    record AvailabilityCalculated() implements DomainEvent {} // could be also
    record DiscountApplied() implements DomainEvent {}
    record InsuranceTraitSelected() {} // this means that availability has been limited. does it deserve to be a domain event? or is it just a state change? if we want to do event sourcing, then it should ---triggers---> AvailabilityChanged
    record OfferAccepted() implements DomainEvent {} // going forward in the conversion funnel
    record PolicyConcluded() implements DomainEvent {}
    record OfferAbandoned() implements DomainEvent {} // equivalent with dropped session
    record OfferExpired() implements DomainEvent {}
}
