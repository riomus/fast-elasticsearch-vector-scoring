package com.liorkn.elasticsearch.script.metrics;

public class DotMetric implements Metric{
    protected final double magnitude;
    protected final double[] inputVector;
    public DotMetric(double[] inputVector){
        double queryVectorNorm = 0.0;
        // compute query inputVector norm once
        for (double v : inputVector) {
            queryVectorNorm += v * v;
        }
        magnitude =  Math.sqrt(queryVectorNorm);
        this.inputVector = inputVector;
    }

    @Override
    public double metric(double[] queryVector) {
        DotMetricResult result = dotMetric(queryVector);
        return result.getDot();
    }

    public DotMetricResult dotMetric(double[] queryVector) {
        double docVectorNorm = 0.0;
        double score = 0.0;
        for (int i = 0; i < queryVector.length; i++) {
            docVectorNorm += queryVector[i]*queryVector[i];
            score += queryVector[i] * inputVector[i];
        }
        if (docVectorNorm == 0 || magnitude == 0){
            return new DotMetricResult(docVectorNorm, 0.0);
        } else {
            return new DotMetricResult(docVectorNorm, score);
        }
    }
}
