package com.liorkn.elasticsearch.script.metrics;

public class EuclideanMetricProvider implements MetricProvider{
    @Override
    public Metric getMetric(double[] inputVector) {
        return new EuclideanMetric(inputVector);
    }
}
