package pl.edu.agh.model;

public enum ExperimentFieldNames { // jak zrobić by dodanie nowego FieldNames wymuszało dodanie nowej wartości enuma.
    // Bez tego można dodać nowe pole w db a funkcje nie będą jej widzieć.
    ALGORITHMS_ENUM(Experiment.FieldNames.ALGORITHMS),
    PROBLEMS_ENUM(Experiment.FieldNames.PROBLEMS),
    METRICS_ENUM(Experiment.FieldNames.METRICS),
    BUDGET_ENUM(Experiment.FieldNames.BUDGET),
    RUNS_ENUM(Experiment.FieldNames.RUNS),
    STATUS_ENUM(Experiment.FieldNames.STATUS),
    START_TIME_ENUM(Experiment.FieldNames.START_TIME),
    END_TIME_ENUM(Experiment.FieldNames.END_TIME),
    ERROR_MESSAGE_ENUM(Experiment.FieldNames.ERROR_MESSAGE);

    private final String value;

    ExperimentFieldNames(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }


}


