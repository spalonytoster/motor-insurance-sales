package com.example.motorinsurancesales.dataprocurement.enrichment;

import static com.example.motorinsurancesales.dataprocurement.enrichment.TransformationPipe.*;

@FunctionalInterface
public interface TransformationPipe<I extends Input, O extends Output> {

    TransformationResult<O> transform(I input);

    interface Input {}
    interface Output {}
}

