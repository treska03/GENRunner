package cli.dto;

import cli.model.ExperimentStatus;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record ExperimentStatusResponseDto(
        @JsonProperty("experimentId") String experimentId,
        @JsonProperty("status") ExperimentStatus status,
        @JsonProperty("errorMessage") String errorMessage
) {
    @JsonCreator
    public ExperimentStatusResponseDto {}
}