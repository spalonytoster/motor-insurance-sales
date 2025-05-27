package com.example.motorinsurancesales.customerjourney;

import com.example.motorinsurancesales.customerjourney.DomainEvent.CalculationContextDataSubmitted;
import com.example.motorinsurancesales.customerjourney.DomainEvent.SalesAbandoned;
import com.example.motorinsurancesales.dataprocurement.CalculationContext;
import com.example.motorinsurancesales.offering.Offering;
import com.example.motorinsurancesales.session.DomainEvent.SessionExpired;
import org.springframework.context.event.EventListener;

import java.util.List;

// this might be whole process orchestrator on higher level
// could possibly oversee the whole sales process and emit events regarding all changes to the process itself
// that should be the main aggregate connecting references to other aggregates representing user data inputs, offerings, checkout


// some idea to implement this is to emit events from sub-domains and catch them here to run commands starting sub-processes in these sub-domains

// shouldn't context recognition be here?
// not sure about it.
// sales channel should but user context like agent context or cc context??
class CustomerJourney {
    List<DomainEvent> events;

    String procurementId;
    String offeringId;
    String checkoutId;

    SalesChannel salesChannel;

//    CalculationContext calculationContext;
//    Offering offering;

    // entry point
    void startNewSales() {}

    // entry point
    void resumeSales() {}

    // indicated that data procurement sub-process (1st step) has completed
    @EventListener
    void handleUserHasFilledAllRequiredData(CalculationContextDataSubmitted event) {
        this.calculationContext = event.calculationContext();
        events.add(event);

        this.offering = new Offering();
        offering.init(event.calculationContext());
    }

    @EventListener
    void pauseSales(SessionExpired event) {
        events.add(new SalesAbandoned());
    }

    // scheduler will invoke this on expired sales that it found
    void archiveExpiredSales() {}
}
