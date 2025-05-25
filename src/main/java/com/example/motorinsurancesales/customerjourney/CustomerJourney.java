package com.example.motorinsurancesales.customerjourney;

import com.example.motorinsurancesales.dataprocurement.CalculationContext;
import com.example.motorinsurancesales.offering.Offering;
import org.springframework.context.event.EventListener;

import java.util.List;

// this might be whole process orchestrator on higher level
// could possibly oversee the whole sales process and emit events regarding all changes to the process itself
// that should be the main aggreage connecting references to other aggregates representing user data inputs, offerings, checkout


// some idea to implement this is to emit events from sub-domains and catch them here to run commands starting sub-processes in these sub-domains

// shouldn't context recognition be here?
// not sure about it.
// sales channel should but user context like agent context or cc context??
class CustomerJourney {
    List<DomainEvents> events;

    SalesChannel salesChannel;

    CalculationContext calculationContext;
    Offering offering;


    @EventListener
    void handleUserHasFilledAllRequiredData(CalculationContext calculationContext) {
        this.calculationContext = calculationContext;
//        events.add(Domain)
    }

    void changeSalesChannel() {}
}
