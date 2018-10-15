package com.liorkn.elasticsearch.script.metrics;

public interface MetricProvider {

    Metric getMetric(double[] inputVector);
}
