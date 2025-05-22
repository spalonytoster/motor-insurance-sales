package com.example.motorinsurancesales.dataprocurement.enrichment;

interface EnrichmentPipe<I, O> {

    TransformationResult<O> transform(I input);
}
