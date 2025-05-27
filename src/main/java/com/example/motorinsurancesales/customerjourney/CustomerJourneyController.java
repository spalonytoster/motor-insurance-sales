package com.example.motorinsurancesales.customerjourney;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/// business process entry points
/// we either find existing sales process with several methods (portal search, vin&birthDate etc.)
/// or create new instance of CustomerJourney (we need to keep context - relations to customer if we have one and reference to user for future resuming)
@RestController
@RequestMapping("/api/customer-journey")
@RequiredArgsConstructor
@Slf4j
class CustomerJourneyController {

    private CustomerJourneyFacade facade;
    private CustomerJourneyRepository repository;

    @GetMapping("/{custViewApiQuotationId}")
    ResponseEntity<CustomerJourneyDetails> getCustomerJourneyByQuotationId(@PathVariable("custViewApiQuotationId") String quotationId) {
        // return read model for customer journey including CustomerJourneyId
        CustomerJourneyDetails journey = facade.getByQuotationId(quotationId);
        return ResponseEntity.ok().body(journey);
    }
}
