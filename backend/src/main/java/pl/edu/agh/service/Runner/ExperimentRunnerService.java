package pl.edu.agh.service.Runner;

import org.bson.types.ObjectId;
import org.moeaframework.core.indicator.StandardIndicator;
import org.springframework.stereotype.Service;
import pl.edu.agh.model.Experiment;
import pl.edu.agh.model.ExperimentResult;
import pl.edu.agh.model.ExperimentStatus;
import pl.edu.agh.repository.ExperimentRepository;
import pl.edu.agh.repository.ExperimentResultRepository;
import pl.edu.agh.service.Runner.Util.ExperimentExecutor;
import reactor.core.publisher.Flux;

import java.time.Instant;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class ExperimentRunnerService {
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
    private final ExperimentRepository experimentRepository;
    private final ExperimentResultRepository resultRepository;

    public ExperimentRunnerService(
            ExperimentRepository experimentRepository,
            ExperimentResultRepository resultRepository
    ) {
        this.experimentRepository = experimentRepository;
        this.resultRepository = resultRepository;
    }

    private static Experiment createExperiment(Set<String> algorithms, Set<String> problems, Set<StandardIndicator> metrics, int budget, int runs) {
        Experiment exp = new Experiment();
        exp.setAlgorithms(algorithms);
        exp.setProblems(problems);
        exp.setMetrics(metrics);
        exp.setBudget(budget);
        exp.setStatus(ExperimentStatus.PENDING);
        exp.setStartTime(Instant.now());
        exp.setRuns(runs);
        return exp;
    }

    public ObjectId runExperiments(Set<String> algorithms, Set<String> problems, Set<StandardIndicator> metrics, int budget, int runs) {
        Experiment exp = createExperiment(algorithms, problems, metrics, budget, runs);
        experimentRepository.save(exp);
        runExperimentAsync(exp);

        return exp.getId();
    }

    public void runExperimentAsync(Experiment experiment) {
        executor.submit(() -> {
            runExperiment(experiment);
        });
    }

    private void runExperiment(Experiment experiment) {
        setExperimentRunning(experiment);

        Flux<ExperimentResult> experimentResults = ExperimentExecutor.getExperimentResults(experiment);

        experimentResults.subscribe(
                resultRepository::save,
                e -> setExperimentError(experiment, (Exception) e),
                () -> setExperimentAsFinished(experiment)
        );
    }

    private void setExperimentRunning(Experiment experiment) {
        experiment.setStatus(ExperimentStatus.RUNNING);
        experimentRepository.save(experiment);
    }

    private void setExperimentAsFinished(Experiment experiment) {
        experiment.setStatus(ExperimentStatus.FINISHED);
        System.out.println(Instant.now());
        experiment.setEndTime(Instant.now());
        experimentRepository.save(experiment);
    }

    private void setExperimentError(Experiment experiment, Exception e) {
        experiment.setStatus(ExperimentStatus.ERROR);
        experiment.setErrorMessage(e.getMessage());
        experimentRepository.save(experiment);
    }
}
