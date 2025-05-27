package com.example.motorinsurancesales.customerjourney;

import com.example.motorinsurancesales.customerjourney.DomainEvent.CalculationContextDataSubmitted;
import com.example.motorinsurancesales.offering.Offering;
import com.example.motorinsurancesales.session.DomainEvent.SessionExpired;
import org.springframework.context.event.EventListener;

class CustomerJourneyFacade {

    private CustomerJourneyRepository repository;

    @EventListener
    void handleUserHasFilledAllRequiredData(CalculationContextDataSubmitted event) {
        CustomerJourney customerJourney = getCustomerJourneyForCurrentSession();

        // im not sure if we can get this from session.
        // we should be given customerJourneyId and then we would retrieve aggregate from session cache (access control there)
        Offering offering = getOfferingForCurrentSession();
        assert offering != null;

        offering.init(event.calculationContext());
    }

    private CustomerJourney getCustomerJourneyForCurrentSession() {
        return null;
    }

    private Offering getOfferingForCurrentSession() {
        return null;
    }

    @EventListener
    void pauseSales(SessionExpired event) {
//        events.add(new DomainEvent.SalesAbandoned());
    }

    public CustomerJourneyDetails getByQuotationId(String quotationId) {
        var journey = repository.getByQuotationId(quotationId);
        return CustomerJourneyDetails.from(journey);
    }
}
