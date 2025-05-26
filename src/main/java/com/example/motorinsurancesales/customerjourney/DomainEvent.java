package com.example.motorinsurancesales.customerjourney;

import com.example.motorinsurancesales.dataprocurement.CalculationContext;

/**
 * These events are higher level events as they give insights on sales overall and will feed analytics.
 * Contrary to each modules' internal events, these events' purpose is not to be able to rebuild state.
 *
 * CustomerJourney events are exposed to sub-domains to emit them.
 * Not sure if it's good though. I think it would be better for CustomerJourney to depend on it's sub-modules.
 * It's already heavily dependent on those whereas sub-domains can remain independent for potential extraction to microservices.
 * If so, events `CalculationContextDataSubmitted`, `OfferAccepted`, `CheckoutComplete` should be moved inwards to their corresponding sum-domain.
 */
public interface DomainEvent {
    record SalesCreated() implements DomainEvent {} // for omnichannel metrics
    record SalesResumed() implements DomainEvent {} // for omnichannel metrics
    record SalesChannelChanged() implements DomainEvent {} // for omnichannel metrics. it's a synthetic domain event just for analytics. it's triggered by SalesCreated event on the condition
    record CalculationContextDataSubmitted(CalculationContext calculationContext) implements DomainEvent {} // it might be needed for analytics and insights to business
    record OfferAccepted() implements DomainEvent {} // this duplicates domain event from quotation, smells stinky. or we can just propagate events from sub-domains to top-level domain and persist on its level
    record SalesAbandoned() implements DomainEvent {} // need to specify channel and what offer has been presented. this is great event for measuring conversion rates
    record CheckoutComplete() implements DomainEvent {}
    record OfferExpired() implements DomainEvent {}
    record SalesTypeRecognized(SalesType salesType) implements DomainEvent {}
}
