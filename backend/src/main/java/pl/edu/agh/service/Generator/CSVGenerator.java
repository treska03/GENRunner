package pl.edu.agh.service.Generator;

import pl.edu.agh.dto.AggregatedExperimentDto;

import java.util.List;

public class CSVGenerator {
    public CSVGenerator() {
    }

    public String generateCsvContent(
            List<AggregatedExperimentDto> aggregatedResults
    ) {
        StringBuilder csvBuilder = new StringBuilder();
        csvBuilder.append("Algorithm,Problem,Iteration,Indicator,Average,Median,StandardDeviation,Count\n");
        for (AggregatedExperimentDto result : aggregatedResults) {
            csvBuilder.append(result.algorithm()).append(",")
                    .append(result.problem()).append(",")
                    .append(result.iteration()).append(",")
                    .append(result.indicator()).append(",")
                    .append(result.average()).append(",")
                    .append(result.median()).append(",")
                    .append(result.standardDeviation()).append(",")
                    .append(result.count()).append("\n");
        }

        return csvBuilder.toString();
    }
}
