package com.example.motorinsurancesales.session;

import com.example.motorinsurancesales.session.DomainEvent.SessionExpired;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;

class SessionManager {
    private ApplicationEventPublisher publisher;

    private List<DomainEvent> events;

    // triggered by expired tomcat session
    void sessionExpired() {
        events.add(new SessionExpired());
    }
}
