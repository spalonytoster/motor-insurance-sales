package com.example.motorinsurancesales.offering;

import java.util.List;

record InsuranceSalesProfile(
        String profileId,
        List<ProfileTrait> traits,
        List<ProfileTag> tags,
        InsuranceCoverage coverage
) {
}
