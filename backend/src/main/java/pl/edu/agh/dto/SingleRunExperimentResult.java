package pl.edu.agh.dto;

import java.util.List;

public record SingleRunExperimentResult(
        String algorithm,
        String problem,
        int runCount,
        List<IterationResult> iterations
) {
}
