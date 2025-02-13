package pl.edu.agh.service.Aggregator.Statistic;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class MedianComputation implements StatsComputationStrategy {
    @Override
    public void execute(DescriptiveStatistics stats, AggregationStats aggregationStats) {
        aggregationStats.setMedian(
                stats.getPercentile(50));
    }
}
