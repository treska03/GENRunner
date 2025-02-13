package pl.edu.agh.dto;

import org.moeaframework.core.indicator.StandardIndicator;

import java.util.Map;

public record IterationResult(
    int iteration,
    Map<StandardIndicator, Double> metricsResults
) {
    @Override
    public int iteration() {
        return iteration;
    }

    @Override
    public Map<StandardIndicator, Double> metricsResults() {
        return metricsResults;
    }
}
