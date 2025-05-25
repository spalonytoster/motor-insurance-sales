package com.example.motorinsurancesales.dataprocurement.enrichment;

import com.example.motorinsurancesales.dataprocurement.enrichment.TransformationPipe.Input;
import com.example.motorinsurancesales.dataprocurement.enrichment.TransformationPipe.Output;
import com.example.motorinsurancesales.dataprocurement.forms.VIN;

import java.util.Map;

class RegisteredTransformationPipes {

    private static final Map<Class<Input>, TransformationPipe<Input, Output>> PIPES = Map.of(
            VIN.class, new VINTransformer(),
            PESEL.class, new CustomerHistoryByPesel()
    );
}
