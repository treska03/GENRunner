package cli.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public enum ExperimentStatus {
    @JsonProperty("PENDING")
    PENDING,
    @JsonProperty("RUNNING")
    RUNNING,
    @JsonProperty("FINISHED")
    FINISHED,
    @JsonProperty("ERROR")
    ERROR;

    @JsonCreator
    public static ExperimentStatus fromValue(String value) {
        for (ExperimentStatus status : ExperimentStatus.values()) {
            if (status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown status: " + value);
    }
}