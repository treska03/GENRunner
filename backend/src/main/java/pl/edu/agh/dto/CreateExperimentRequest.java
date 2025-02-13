package pl.edu.agh.dto;

import org.moeaframework.core.indicator.StandardIndicator;

import java.util.Set;

public record CreateExperimentRequest(
        Set<String> algorithms,
        Set<String> problems,
        Set<StandardIndicator> metrics,
        int budget,
        int runs
) {
}
