package pl.edu.agh.model;

import org.bson.types.ObjectId;
import org.moeaframework.core.indicator.StandardIndicator;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.Objects;
import java.util.Set;

@Document(collection = "experiments")
public class Experiment {
    @Id
    private ObjectId id;
    @Field(name = FieldNames.ALGORITHMS)
    private Set<String> algorithms;

    @Field(name = FieldNames.PROBLEMS)
    private Set<String> problems;

    @Field(name = FieldNames.METRICS)
    private Set<StandardIndicator> metrics;

    @Field(name = FieldNames.BUDGET)
    private int budget;

    @Field(name = FieldNames.RUNS)
    private int runs;

    @Field(name = FieldNames.STATUS)
    private ExperimentStatus status;

    @Field(name = FieldNames.START_TIME)
    private Instant startTime;

    @Field(name = FieldNames.END_TIME)
    private Instant endTime;

    @Field(name = FieldNames.ERROR_MESSAGE)
    private String errorMessage;
    public Experiment(ObjectId id, Set<String> algorithms, Set<String> problems, Set<StandardIndicator> metrics, int budget, int runs, ExperimentStatus status, Instant startTime, Instant endTime, String errorMessage) {
        this.id = id;
        this.algorithms = algorithms;
        this.problems = problems;
        this.metrics = metrics;
        this.budget = budget;
        this.runs = runs;
        this.status = ExperimentStatus.RUNNING;
        this.startTime = Instant.now();
        this.endTime = null;
        this.errorMessage = null;
    }
    public Experiment() {
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public Set<String> getAlgorithms() {
        return algorithms;
    }

    public void setAlgorithms(Set<String> algorithms) {
        this.algorithms = algorithms;
    }

    public Set<String> getProblems() {
        return problems;
    }

    public void setProblems(Set<String> problems) {
        this.problems = problems;
    }

    public Set<StandardIndicator> getMetrics() {
        return metrics;
    }

    public void setMetrics(Set<StandardIndicator> metrics) {
        this.metrics = metrics;
    }

    public int getBudget() {
        return budget;
    }

    public void setBudget(int budget) {
        this.budget = budget;
    }

    public int getRuns() {
        return runs;
    }

    public void setRuns(int runs) {
        this.runs = runs;
    }

    public ExperimentStatus getStatus() {
        return status;
    }

    public void setStatus(ExperimentStatus status) {
        this.status = status;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Experiment that = (Experiment) o;
        return budget == that.budget && runs == that.runs && Objects.equals(id, that.id) && Objects.equals(algorithms, that.algorithms) && Objects.equals(problems, that.problems) && Objects.equals(metrics, that.metrics) && status == that.status && Objects.equals(startTime, that.startTime) && Objects.equals(endTime, that.endTime) && Objects.equals(errorMessage, that.errorMessage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, algorithms, problems, metrics, budget, runs, status, startTime, endTime, errorMessage);
    }

    public static final class FieldNames {
        public static final String ALGORITHMS = "algorithms";
        public static final String PROBLEMS = "problems";
        public static final String METRICS = "metrics";
        public static final String BUDGET = "budget";
        public static final String RUNS = "runs";
        public static final String STATUS = "status";
        public static final String START_TIME = "start_time";
        public static final String END_TIME = "end_time";
        public static final String ERROR_MESSAGE = "error_Message";
    }

}
