package com.example.motorinsurancesales.customerjourney;

interface DomainEvents {
    record SalesChannelChanged() {} // for omnichannel metrics
    record CalculationContextDataSubmitted() {} // it might be needed for analytics and insights to business
    record OfferAccepted() {} // this duplicates domain event from offering, smells stinky. or we can just propagate events from sub-domains to top-level domain and persist on its level
    record OfferingAbandoned() {} // need to specify channel and what offer has been presented. this is great event for measuring conversion rates
}
