package pl.edu.agh.service.Aggregator.Util;

import org.moeaframework.core.indicator.StandardIndicator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MetricResults {
    private final StandardIndicator metric;
    private final Integer iteration;
    private final List<Double> resultValues;

    public MetricResults(StandardIndicator metric, Integer iteration) {
        this.metric = metric;
        this.iteration = iteration;
        resultValues = new ArrayList<>();
    }

    public Integer getIteration() {
        return iteration;
    }

    public StandardIndicator getMetric() {
        return metric;
    }

    public List<Double> getResultValues() {
        return resultValues;
    }

    public void addValue(Double value) {
        resultValues.add(value);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MetricResults that = (MetricResults) o;
        return metric == that.metric && Objects.equals(iteration, that.iteration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(metric, iteration);
    }
}
