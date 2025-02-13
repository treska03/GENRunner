package pl.edu.agh.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import pl.edu.agh.dto.IterationResult;

import java.util.List;

@Document(collection = "results")
public class ExperimentResult {
    @Id
    private ObjectId id;
    @Field(name = FieldNames.EXPERIMENT)
    private Experiment experiment;
    @Field(name = FieldNames.ALGORITHM)
    private String algorithm;
    @Field(name = FieldNames.PROBLEM)
    private String problem;
    @Field(name = FieldNames.ITERATION_RESULTS)
    private List<IterationResult> iterationResults;
    @Field(name = FieldNames.RUN_COUNT)
    private int runCount;

    public ExperimentResult(ObjectId id, Experiment experiment, String algorithm, String problem, List<IterationResult> iterationResults, int runCount) {
        this.id = id;
        this.experiment = experiment;
        this.algorithm = algorithm;
        this.problem = problem;
        this.iterationResults = iterationResults;
        this.runCount = runCount;
    }

    public ExperimentResult() {
    }

    public int getBudget(){
        return experiment.getBudget();
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public Experiment getExperiment() {
        return experiment;
    }

    public void setExperiment(Experiment experiment) {
        this.experiment = experiment;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public String getProblem() {
        return problem;
    }

    public void setProblem(String problem) {
        this.problem = problem;
    }

    public List<IterationResult> getIterationResults() {
        return iterationResults;
    }

    public void setIterationResults(List<IterationResult> iterationResults) {
        this.iterationResults = iterationResults;
    }

    public int getRunCount() {
        return runCount;
    }

    public void setRunCount(int runCount) {
        this.runCount = runCount;
    }

    public static final class FieldNames {
        public static final String ID = "id";
        public static final String EXPERIMENT = "experiment";
        public static final String ALGORITHM = "algorithm";
        public static final String PROBLEM = "problem";
        public static final String ITERATION_RESULTS = "iteration_result";
        public static final String RUN_COUNT = "run_count";
    }
}