package com.example.motorinsurancesales.offering;

import com.example.motorinsurancesales.dataprocurement.CalculationContext;
import com.example.motorinsurancesales.offering.DomainEvent.AvailabilityCalculated;
import com.example.motorinsurancesales.offering.DomainEvent.OfferAccepted;

import java.util.List;

// Current problem to solve: how to run concurrent offering simulations?
// price simulations and operations on a single offering?
// multiple offerings initialized with same CalculationContext? what's better? need to discover sensible metrics to rate solution

// the name Offering has been chosen instead of Quotation to differentiate from quotation as a pre-policy entity in TIA
public class Offering {

    private PricingEngineService pricingEngineService;

    private CalculationContext calculationContext;
    List<DomainEvent> events;
    List<InsuranceCoverage> reachableCoverageOptions;

    public void init(CalculationContext calculationContext) {
        this.calculationContext = calculationContext;

        // initial query to pricing engine
        var profiles = calculateAvailableCoverageOptions(calculationContext);
    }

    void calculateAvailableCoverageOptions(CalculationContext calculationContext) {
        // call Earnix via TIA
        // union coverage option types from all profiles
        // every coverage option has traits from ProfileId as requirements to be available
        pricingEngineService.calculateOptionsAndPricing(calculationContext);
        events.add(new AvailabilityCalculated());
    }

    void applyDiscount() {
        // add reference to discount
        // if it's agent discount, lock it
        // DANGER ZONE - if we lock it here for the sake of offering that will get abandoned,
        // that discount will be frozen until offer is dropped

        // alternative - treat offering as simulation with price projection IF discount would be applied
        // then consume that discount only when offer gets accepted (or transaction is finalized?
        // but then does checkout need to know about discounts??? that's a very bad idea)
    }

    // UX-wise - possible to list some coverage options that customer might be interested in
    // this we might use to find best matching InsuranceProfile
    // domain-wise it's APK...
    // so better to
    void selectCoverageOption() {

    }

//    void

    void dropOffering() {
        // release discount
    }

    void acceptOffer() {
        events.add(new OfferAccepted());
    }

}
