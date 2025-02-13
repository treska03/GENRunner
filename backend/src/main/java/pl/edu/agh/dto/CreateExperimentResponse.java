package pl.edu.agh.dto;

import pl.edu.agh.model.ExperimentStatus;

public record CreateExperimentResponse(
    String experimentId,
    ExperimentStatus status
) {
}
