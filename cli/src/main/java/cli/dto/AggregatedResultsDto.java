package cli.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record AggregatedResultsDto(
        @JsonProperty("algorithm") String algorithm,
        @JsonProperty("problem") String problem,
        @JsonProperty("iteration") int iteration,
        @JsonProperty("indicator") String indicator,
        @JsonProperty("average") double average,
        @JsonProperty("median") double median,
        @JsonProperty("standardDeviation") double standardDeviation,
        @JsonProperty("count") int count
) {
    @JsonCreator
    public AggregatedResultsDto {}
}
