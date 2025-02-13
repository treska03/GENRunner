package cli.dto;

import cli.model.ExperimentStatus;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record CreateExperimentResponseDto(
        @JsonProperty("experimentId") String experimentId,
        @JsonProperty("status") ExperimentStatus status
) {
    @JsonCreator
    public CreateExperimentResponseDto {}
}