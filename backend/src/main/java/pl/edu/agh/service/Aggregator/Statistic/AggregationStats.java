package pl.edu.agh.service.Aggregator.Statistic;

public class AggregationStats {
    private double mean;
    private double median;
    private double standardDeviation;
    private final int count;

    public AggregationStats(int count) {
        this.count = count;
    }

    public double getMean() {
        return mean;
    }

    public void setMean(double mean) {
        this.mean = mean;
    }

    public double getMedian() {
        return median;
    }

    public void setMedian(double median) {
        this.median = median;
    }

    public double getStandardDeviation() {
        return standardDeviation;
    }

    public void setStandardDeviation(double standardDeviation) {
        this.standardDeviation = standardDeviation;
    }

    public int getCount() {
        return count;
    }

    @Override
    public String toString() {
        return "AggregationStats{" +
                "mean=" + mean +
                ", median=" + median +
                ", standardDeviation=" + standardDeviation +
                ", count=" + count +
                '}';
    }
}
