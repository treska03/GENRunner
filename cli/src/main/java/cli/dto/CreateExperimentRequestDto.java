package cli.dto;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

public record CreateExperimentRequestDto(
        @JsonProperty("algorithms") Set<String> algorithms,
        @JsonProperty("problems") Set<String> problems,
        @JsonProperty("metrics") Set<String> metrics,
        @JsonProperty("budget") int budget,
        @JsonProperty("runs") int runs
) {
    @JsonCreator
    public CreateExperimentRequestDto {}

    public String toJson() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (Exception e) {
            e.printStackTrace();
            return "{}";
        }
    }
}
