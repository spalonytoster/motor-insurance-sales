package com.example.motorinsurancesales.customerjourney;

import com.example.motorinsurancesales.dataprocurement.CalculationContext;

/**
 * These events are higher level events as they give insights on sales overall and will feed analytics.
 * Contrary to each modules' internal events, these events' purpose is not to be able to rebuild state.
 */
interface DomainEvents {
    record SalesCreated() implements DomainEvents {} // for omnichannel metrics
    record SalesResumed() implements DomainEvents {} // for omnichannel metrics
    record SalesChannelChanged() implements DomainEvents {} // for omnichannel metrics. it's a synthetic domain event just for analytics. it's triggered by SalesCreated event on the condition
    record CalculationContextDataSubmitted(CalculationContext calculationContext) implements DomainEvents {} // it might be needed for analytics and insights to business
    record OfferAccepted() implements DomainEvents {} // this duplicates domain event from quotation, smells stinky. or we can just propagate events from sub-domains to top-level domain and persist on its level
    record SalesAbandoned() implements DomainEvents {} // need to specify channel and what offer has been presented. this is great event for measuring conversion rates
    record CheckoutComplete() implements DomainEvents {}
    record OfferExpired() implements DomainEvents {}
}
