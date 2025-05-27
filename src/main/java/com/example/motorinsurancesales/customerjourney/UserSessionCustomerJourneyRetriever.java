package com.example.motorinsurancesales.customerjourney;

import com.example.motorinsurancesales.session.SessionService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.web.context.annotation.SessionScope;

@SessionScope
@RequiredArgsConstructor
class UserSessionCustomerJourneyRetriever {
    private final CustomerJourneyRepository repository;
    private final SessionService sessionService;

    private CustomerJourney customerJourney;

    // shouldn't we have some kind of cache to map user session to a collection of running customer journeys (sales)?

    @PostConstruct
    public void init() {
        var sessionId = sessionService.getCurrentSessionId();

        // how to match customer journey by sessionId? find salesman context by sessionId?
        // but sales agent can have multiple sales running in a single session

//        this.customerJourney = repository.getById(sessionId);
    }
}
