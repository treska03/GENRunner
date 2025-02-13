package cli.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;

public record GroupedExperimentRequestDto(
        @JsonProperty("groupName") String groupName,
        @JsonProperty("experimentsIdToAdd") Set<String> experimentsIdToAdd
) {
    @JsonCreator
    public GroupedExperimentRequestDto {
    }
}
