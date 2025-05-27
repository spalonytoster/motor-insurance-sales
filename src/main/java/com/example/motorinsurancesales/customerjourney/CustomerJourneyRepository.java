package com.example.motorinsurancesales.customerjourney;

import java.util.Optional;

interface CustomerJourneyRepository {
    Optional<CustomerJourney> getById(String id);

    CustomerJourney getByQuotationId(String quotationId);
}
