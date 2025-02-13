package pl.edu.agh.service.Aggregator.Statistic;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class STDComputation implements StatsComputationStrategy{

    @Override
    public void execute(DescriptiveStatistics stats, AggregationStats aggregationStats) {
        aggregationStats.setStandardDeviation(stats.getStandardDeviation());
    }
}
