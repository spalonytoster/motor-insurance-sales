package com.example.motorinsurancesales.customerjourney;

import lombok.Builder;

@Builder
record CustomerJourneyDetails() {
    public static CustomerJourneyDetails from(CustomerJourney journey) {
        return CustomerJourneyDetails.builder().build();
    }
}
