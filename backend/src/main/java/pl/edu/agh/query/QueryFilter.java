package pl.edu.agh.query;

public record QueryFilter(String fieldName, Operation operation, Object value) {
}

