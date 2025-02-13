package pl.edu.agh.service.Aggregator.Statistic;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.Arrays;
import java.util.List;

public class StatsContext {
    private final List<StatsComputationStrategy> strategy;
    private final DescriptiveStatistics stats = new DescriptiveStatistics();

    public StatsContext(List<StatsComputationStrategy> strategy) {
        this.strategy = strategy;
    }

    public void addNewStrategy(StatsComputationStrategy strategy) {
        this.strategy.add(strategy);
    }

    public AggregationStats aggregate(List<Double> inputNumbers) {
        AggregationStats aggregationStats = new AggregationStats(inputNumbers.size());
        Double[] inputArray = inputNumbers.toArray(new Double[0]);
        Arrays.stream(inputArray).forEach(stats::addValue);

        for (StatsComputationStrategy strategyI : strategy) {
            strategyI.execute(stats, aggregationStats);
        }
        stats.clear();
        return aggregationStats;
    }
}
