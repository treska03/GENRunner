package cli.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public record ExperimentResultsResponseDto(
        @JsonProperty("experimentId") String experimentId,
        @JsonProperty("results") List<AlgorithmResult> results
) {
    @JsonCreator
    public ExperimentResultsResponseDto {}

    public static record AlgorithmResult(
            @JsonProperty("algorithm") String algorithm,
            @JsonProperty("problem") String problem,
            @JsonProperty("runCount") int runCount,
            @JsonProperty("iterations") List<IterationResult> iterations
    ) {
        @JsonCreator
        public AlgorithmResult {}
    }

    public static record IterationResult(
            @JsonProperty("iteration") int iteration,
            @JsonProperty("metricsResults") Map<String, Double> metricsResults
    ) {
        @JsonCreator
        public IterationResult {}
    }
}