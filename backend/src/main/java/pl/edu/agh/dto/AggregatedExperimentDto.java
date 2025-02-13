package pl.edu.agh.dto;

public record AggregatedExperimentDto(String algorithm, String problem, int iteration, String indicator, double average,
                                      double median, double standardDeviation, int count) {
}