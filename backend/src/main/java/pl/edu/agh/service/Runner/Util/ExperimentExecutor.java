package pl.edu.agh.service.Runner.Util;

import org.moeaframework.Executor;
import org.moeaframework.Instrumenter;
import org.moeaframework.analysis.collector.Observation;
import org.moeaframework.core.indicator.StandardIndicator;
import pl.edu.agh.dto.IterationResult;
import pl.edu.agh.exception.NoExperimentsMetricsToCollectException;
import pl.edu.agh.model.Experiment;
import pl.edu.agh.model.ExperimentResult;
import reactor.core.publisher.Flux;

import java.util.*;

public class ExperimentExecutor {

    public static final int ITERATION_STEPS = 100;

    public static Flux<ExperimentResult> getExperimentResults(Experiment experiment) {
        return Flux.create(sink -> {
            Set<StandardIndicator> metricsToCollect = null;

            try {
                metricsToCollect = getMetricsToCollect(experiment);
            } catch (NoExperimentsMetricsToCollectException e) {
                sink.error(e);
            }

            for (String algorithm : experiment.getAlgorithms()) {
                for (String problem : experiment.getProblems()) {
                    for (int runCount = 0; runCount < experiment.getRuns(); runCount++) { // maybe rename it
                        List<IterationResult> iterationResults = new ArrayList<>();
                        Instrumenter metricsCollector = new Instrumenter()
                                .withProblem(problem)
                                .withFrequency(ITERATION_STEPS)
                                .attachAll();

                        Executor algorithmExecutor = new Executor()
                                .withProblem(problem)
                                .withAlgorithm(algorithm)
                                .withMaxEvaluations(experiment.getBudget())
                                .withInstrumenter(metricsCollector);

                        algorithmExecutor.run();

                        for (int iteration = ITERATION_STEPS; iteration <= experiment.getBudget(); iteration += ITERATION_STEPS) {


                            Map<StandardIndicator, Double> collectedMetrics = new HashMap<>();

                            Observation latestObservation = metricsCollector.getObservations().at(iteration);
                            if (latestObservation != null) {
                                for (String metricName : metricsCollector.getObservations().keys()) {
                                    try {
                                        StandardIndicator indicator = StandardIndicator.valueOf(metricName);
                                        if (metricsToCollect.contains(indicator)) {
                                            Double value = (Double) latestObservation.get(metricName);
                                            if (value != null) {
                                                collectedMetrics.put(indicator, value);
                                            }
                                        }
                                    } catch (IllegalArgumentException ignored) {
                                    }
                                }
                            }

                            IterationResult iterationResult = new IterationResult(iteration, collectedMetrics);
                            iterationResults.add(iterationResult);
                        }
                        ExperimentResult experimentResult = getExperimentResult(experiment, problem, algorithm, iterationResults, runCount);
                        sink.next(experimentResult);
                    }
                }
            }
            sink.complete();
        });
    }

    private static Set<StandardIndicator> getMetricsToCollect(Experiment experiment) throws NoExperimentsMetricsToCollectException {
        Set<StandardIndicator> metricsToCollect = experiment.getMetrics();

        if (metricsToCollect.isEmpty()) {
            throw new NoExperimentsMetricsToCollectException("No metrics to collect left. Make sure to provide correct ones.");
        }
        return metricsToCollect;
    }

    private static ExperimentResult getExperimentResult(Experiment experiment, String problem, String algorithm, List<IterationResult> iterationResults, int runCount) {
        ExperimentResult experimentResult = new ExperimentResult();
        experimentResult.setExperiment(experiment);
        experimentResult.setAlgorithm(algorithm);
        experimentResult.setProblem(problem);
        experimentResult.setIterationResults(iterationResults);
        experimentResult.setRunCount(runCount);
        return experimentResult;
    }
}
