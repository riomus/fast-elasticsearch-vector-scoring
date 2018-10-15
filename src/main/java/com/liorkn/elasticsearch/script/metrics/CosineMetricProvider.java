package com.liorkn.elasticsearch.script.metrics;

public class CosineMetricProvider implements MetricProvider{
    @Override
    public Metric getMetric(double[] inputVector) {
        return new CosineMetric(inputVector);
    }
}
