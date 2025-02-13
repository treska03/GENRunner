package pl.edu.agh.exception;

public class ExperimentGroupNotFoundException extends RuntimeException {
    public ExperimentGroupNotFoundException(String groupName) {
        super("Experiment group with groupName=[" + groupName + "] was not found.");
    }
}
