package cli.model;

import java.util.Map;

public class IterationResult {
    private int iteration;
    private Map<String, Double> metricsResults;

    public IterationResult(int iteration, Map<String, Double> metricsResults) {
        this.iteration = iteration;
        this.metricsResults = metricsResults;
    }

    public IterationResult() {
    }

    public int getIteration() {
        return iteration;
    }

    public void setIteration(int iteration) {
        this.iteration = iteration;
    }

    public Map<String, Double> getMetricsResults() {
        return metricsResults;
    }

    public void setMetricsResults(Map<String, Double> metricsResults) {
        this.metricsResults = metricsResults;
    }
}
