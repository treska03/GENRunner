package pl.edu.agh.dto;

import pl.edu.agh.model.ExperimentStatus;

public record ExperimentStatusResponse(
    String experimentId,
    ExperimentStatus status,
    String errorMessage
) {
}
