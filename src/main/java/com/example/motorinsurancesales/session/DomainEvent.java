package com.example.motorinsurancesales.session;

public interface DomainEvent {
    record SessionExpired() implements DomainEvent {}
}