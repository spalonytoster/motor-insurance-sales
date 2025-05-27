///
/// not sure yet, but IMO infrastructure of tia services should recognize a situation in which case
/// has been filed and then infra should call this domain to handle code flow:
/// emit domain event about case that has been filed AND throw specialized runtime exception
///
///
///
/// Is it smart to centralize cases handling?
/// What would happen if every process step (now only dataprocurement and offering) would create their own DomainEvent
/// e.g. CaseFiled? Because whose domnain it is? I mean if I am the offering module then when I recognize that case
/// has been filed, I handle the case myself according to my domain. I, myself, am persisting an event that case has
/// been filed in my area.
/// I only want to reuse some infrastructure part of Cases domain.
///
///
package com.example.motorinsurancesales.underwritingcases;