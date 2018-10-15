package com.liorkn.elasticsearch.script.metrics;

public class TSSSMetricProvider implements MetricProvider{
    @Override
    public Metric getMetric(double[] inputVector) {
        return new TSSSMetric(inputVector);
    }
}
