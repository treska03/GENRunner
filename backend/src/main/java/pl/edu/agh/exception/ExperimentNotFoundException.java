package pl.edu.agh.exception;

import org.bson.types.ObjectId;

public class ExperimentNotFoundException extends RuntimeException {
    public ExperimentNotFoundException(String message) {
        super(message);
    }

    public ExperimentNotFoundException(ObjectId id) {
        super("Experiment with id=[" + id.toString() + "] was not found.");
    }
}
