package cli.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record ExperimentResponseDto(
        @JsonProperty("id") String id,
        @JsonProperty("algorithms") List<String> algorithms,
        @JsonProperty("problems") List<String> problems,
        @JsonProperty("metrics") List<String> metrics,
        @JsonProperty("budget") int budget,
        @JsonProperty("runs") int runs,
        @JsonProperty("status") String status,
        @JsonProperty("startTime") String startTime,
        @JsonProperty("endTime") String endTime,
        @JsonProperty("errorMessage") String errorMessage
) {
    @JsonCreator
    public ExperimentResponseDto {}
}
