package com.example.motorinsurancesales.dataprocurement.enrichment;

record TransformationResult<T>(
        String status,
        T data
) {

    static <T> TransformationResult<T> ok(T data) {
        return new TransformationResult<>("OK", data);
    }
}
