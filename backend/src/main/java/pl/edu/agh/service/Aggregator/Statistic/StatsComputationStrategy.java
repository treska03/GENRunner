package pl.edu.agh.service.Aggregator.Statistic;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public interface StatsComputationStrategy {
    void execute(DescriptiveStatistics stats, AggregationStats aggregationStats);
}
