package pl.edu.agh.service;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.moeaframework.core.indicator.StandardIndicator;
import org.moeaframework.core.spi.ProviderNotFoundException;
import pl.edu.agh.dto.IterationResult;
import pl.edu.agh.exception.NoExperimentsMetricsToCollectException;
import pl.edu.agh.model.Experiment;
import pl.edu.agh.model.ExperimentResult;
import pl.edu.agh.service.Runner.Util.ExperimentExecutor;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ExperimentExecutorTest {
    private final ExperimentExecutor tested = new ExperimentExecutor();
    private final Set<String> correctAlgorithms = Set.of("VEGA", "AMOSA");
    private final Set<String> correctProblems = Set.of("ZDT1", "ZDT2");
    private final Set<StandardIndicator> correctMetrics = Set.of(StandardIndicator.Hypervolume, StandardIndicator.GenerationalDistance);
    private final int correctBudget = 1000;
    private final int correctRuns = 2;


    @Test
    public void shouldExecuteWithCorrectExperiment() {
        // given
        Experiment exp = new Experiment();
        exp.setAlgorithms(correctAlgorithms);
        exp.setProblems(correctProblems);
        exp.setMetrics(correctMetrics);
        exp.setBudget(correctBudget);
        exp.setRuns(correctRuns);
        ExperimentResult expectedResult0 = createExperimentResult(exp,getAt(correctAlgorithms, 0), getAt(correctProblems, 0), 0);
        ExperimentResult expectedResult1 = createExperimentResult(exp,getAt(correctAlgorithms, 0), getAt(correctProblems, 0), 1);
        ExperimentResult expectedResult2 = createExperimentResult(exp,getAt(correctAlgorithms, 0), getAt(correctProblems, 1), 0);
        ExperimentResult expectedResult3 = createExperimentResult(exp,getAt(correctAlgorithms, 0), getAt(correctProblems, 1), 1);
        ExperimentResult expectedResult4 = createExperimentResult(exp,getAt(correctAlgorithms, 1), getAt(correctProblems, 0), 0);
        ExperimentResult expectedResult5 = createExperimentResult(exp,getAt(correctAlgorithms, 1), getAt(correctProblems, 0), 1);
        ExperimentResult expectedResult6 = createExperimentResult(exp,getAt(correctAlgorithms, 1), getAt(correctProblems, 1), 0);
        ExperimentResult expectedResult7 = createExperimentResult(exp,getAt(correctAlgorithms, 1), getAt(correctProblems, 1), 1);

        // when
        Flux<ExperimentResult> experimentResults = tested.getExperimentResults(exp);

        // then
        StepVerifier.create(experimentResults)
                .expectNextMatches(result -> matchesWith(result, expectedResult0))
                .expectNextMatches(result -> matchesWith(result, expectedResult1))
                .expectNextMatches(result -> matchesWith(result, expectedResult2))
                .expectNextMatches(result -> matchesWith(result, expectedResult3))
                .expectNextMatches(result -> matchesWith(result, expectedResult4))
                .expectNextMatches(result -> matchesWith(result, expectedResult5))
                .expectNextMatches(result -> matchesWith(result, expectedResult6))
                .expectNextMatches(result -> matchesWith(result, expectedResult7))
                .expectComplete()
                .verify();
    }

    @Test
    public void shouldThrowOnIncorrectAlgorithm() {
        // given
        Experiment exp = new Experiment();
        exp.setAlgorithms(Set.of("IncorrectSkibidiAlgorithm"));
        exp.setProblems(correctProblems);
        exp.setMetrics(correctMetrics);
        exp.setBudget(correctBudget);
        exp.setRuns(correctRuns);

        // when
        Flux<ExperimentResult> experimentResults = tested.getExperimentResults(exp);

        // then
        StepVerifier.create(experimentResults)
                .expectErrorMatches(throwable -> throwable instanceof ProviderNotFoundException)
                .verify();
    }

    @Test
    public void shouldThrowOnIncorrectProblem() {
        // given
        Experiment exp = new Experiment();
        exp.setAlgorithms(correctAlgorithms);
        exp.setProblems(Set.of("IncorrectSkibidiProblem"));
        exp.setMetrics(correctMetrics);
        exp.setBudget(correctBudget);
        exp.setRuns(correctRuns);

        // when
        Flux<ExperimentResult> experimentResults = tested.getExperimentResults(exp);

        // then
        StepVerifier.create(experimentResults)
                .expectErrorMatches(throwable -> throwable instanceof ProviderNotFoundException)
                .verify();
    }

    @Test
    public void shouldThrowOnEmptyMetricsToCollect() {
        // given
        Experiment exp = new Experiment();
        exp.setAlgorithms(correctAlgorithms);
        exp.setProblems(correctProblems);
        exp.setMetrics(Set.of());
        exp.setBudget(correctBudget);
        exp.setRuns(correctRuns);

        // when
        Flux<ExperimentResult> experimentResults = tested.getExperimentResults(exp);

        // then
        StepVerifier.create(experimentResults)
                .expectErrorMatches(throwable -> throwable instanceof NoExperimentsMetricsToCollectException)
                .verify();
    }


    private ExperimentResult createExperimentResult(Experiment exp, String algorithm, String problem, int runCount) {
        List<IterationResult> iterationResult = new ArrayList<>();
        for(int iteration=tested.ITERATION_STEPS; iteration<=exp.getBudget(); iteration+=tested.ITERATION_STEPS) {
            iterationResult.add(
                    new IterationResult(
                            iteration,
                            exp.getMetrics().stream().collect(Collectors.toMap(Function.identity(), indicator -> 0.0))
                    )
            );
        }
        return new ExperimentResult(
                new ObjectId(new Date()),
                exp,
                algorithm,
                problem,
                iterationResult,
                runCount
        );
    }

    private <T> T getAt(Set<T> set, int idx) {
        return set.stream().toList().get(idx);
    }

    private boolean matchesWith(ExperimentResult result, ExperimentResult other) {
        return Objects.equals(result.getAlgorithm(), other.getAlgorithm()) &&
                Objects.equals(result.getProblem(), other.getProblem()) &&
                Objects.equals(result.getExperiment(), other.getExperiment()) &&
                Objects.equals(result.getRunCount(), other.getRunCount()) &&
                iterationResultsMatch(result.getIterationResults(), other.getIterationResults());

    }

    private boolean iterationResultsMatch(List<IterationResult> result, List<IterationResult> other) {
        if(result.size() != other.size()) return false;
        for(int idx=0; idx<result.size(); idx++) {
            IterationResult resultAtIdx = result.get(idx);
            IterationResult otherAtIdx = other.get(idx);
            if(resultAtIdx.iteration() != otherAtIdx.iteration() ||
                !Objects.equals(resultAtIdx.metricsResults().keySet(), otherAtIdx.metricsResults().keySet())) {
                return false;
            }
        }
        return true;
    }
}
