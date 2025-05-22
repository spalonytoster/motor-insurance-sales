package com.example.motorinsurancesales.offering;

import java.util.List;

// this might be whole process orchestrator on higher level
// could possibly oversee the whole sales process and emit events regarding all changes to the process itself
// that should be the main aggreage connecting references to other aggregates representing user data inputs, offerings, checkout
class CustomerJourney {

    CustomerContext customerContext;
    SalesChannel salesChannel;

    List<DomainEvents> events;

    void changeSalesChannel() {}
}
