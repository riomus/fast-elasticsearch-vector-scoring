package com.liorkn.elasticsearch.script.metrics;

public class DotMetricResult {
    double dotVectorNorm;
    double dot;
    public DotMetricResult(double dotVectorNorm, double dot){
        this.dotVectorNorm = dotVectorNorm;
                this.dot = dot;
    }

    public double getDotVectorNorm() {
        return dotVectorNorm;
    }

    public double getDot() {
        return dot;
    }
}
