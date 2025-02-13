package pl.edu.agh.dto;

import java.util.List;

public record ExperimentResultsResponse(
        String experimentId,
        List<SingleRunExperimentResult> results
) {
}
