package cli.model;

public enum OutputType {
    DEFAULT,
    CSV,
    CHART;

    public static OutputType fromValue(String value) {
        for (OutputType type : OutputType.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        return DEFAULT;
    }

    public static String toValue(OutputType type) {
        return type.name();
    }
}
