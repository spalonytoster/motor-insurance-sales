package com.example.motorinsurancesales.session;

// just leaving a note that we need to implement session handling with sticky session mechanism.
// that's because aggregates will be too beefy to read from database everytime

// alternative here is to split into smaller aggregates because .g. when we are at checkout stage,
// we do not need to retrieve offering until customer want to change coverage.
// a significant challenge would be splitting offering into smaller aggregates though. not sure if possible.

// invariant here is that there cannot be 2 sessions for the same CustomerJourney
class Session {
}
