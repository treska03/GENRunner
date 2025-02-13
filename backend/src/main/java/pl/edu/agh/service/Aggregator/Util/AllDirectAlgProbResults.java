package pl.edu.agh.service.Aggregator.Util;

import org.moeaframework.core.indicator.StandardIndicator;
import pl.edu.agh.dto.IterationResult;

import java.util.*;

public class AllDirectAlgProbResults {
    private final String algorithm;
    private final String problem;
    private final Map<Integer, List<IterationResult>> everyIterationResult;
    private final Map<String, MetricResults> iterationResultsByMetric;

    public AllDirectAlgProbResults(String algorithm, String problem) {
        this.algorithm = algorithm;
        this.problem = problem;
        everyIterationResult = new LinkedHashMap<>();
        iterationResultsByMetric = new HashMap<>();
    }

    public Map<String, MetricResults> getAllMetricResults() {
        return iterationResultsByMetric;
    }

    public void addIterationResult(IterationResult iterationResult) {
        everyIterationResult.computeIfAbsent(iterationResult.iteration(), key -> new ArrayList<>()).add(iterationResult);
    }

    public void compute() {
        for (Integer iterationNumber : everyIterationResult.keySet()) {
            for (IterationResult iterationResult : everyIterationResult.get(iterationNumber)) {
                for (Map.Entry<StandardIndicator, Double> entry : iterationResult.metricsResults().entrySet()) {

                    StandardIndicator key = entry.getKey();
                    Double value = entry.getValue();
                    String metricValue = key + "#" + iterationNumber;

                    iterationResultsByMetric.computeIfAbsent(metricValue, k -> new MetricResults(key, iterationNumber)).addValue(value);
                }
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AllDirectAlgProbResults that = (AllDirectAlgProbResults) o;
        return Objects.equals(algorithm, that.algorithm) && Objects.equals(problem, that.problem);
    }

    @Override
    public int hashCode() {
        return Objects.hash(algorithm, problem);
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public String getProblem() {
        return problem;
    }
}
