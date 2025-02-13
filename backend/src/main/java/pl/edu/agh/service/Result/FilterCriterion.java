package pl.edu.agh.service.Result;

import org.moeaframework.core.indicator.StandardIndicator;
import pl.edu.agh.model.ExperimentFieldNames;
import pl.edu.agh.query.MongoQueryBuilder;
import pl.edu.agh.query.Operation;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class FilterCriterion {
    private final List<String> criterion;
    private final ExperimentFieldNames fieldNames;

    public FilterCriterion(List<String> criterion, ExperimentFieldNames fieldNames) {
        this.criterion = criterion;
        this.fieldNames = fieldNames;
    }

    public void apply(MongoQueryBuilder builder) {
        if (Objects.equals(fieldNames.toString(), ExperimentFieldNames.METRICS_ENUM.getValue())) {
            builder.addFilter(fieldNames.getValue(), Operation.IN, convertMetricsToIndicators(criterion));
        } else {
            builder.addFilter(fieldNames.getValue(), Operation.IN, criterion);
        }
    }

    private Set<StandardIndicator> convertMetricsToIndicators(List<String> metrics) {
        return metrics != null ? metrics.stream()
                .map(StandardIndicator::valueOf)
                .collect(Collectors.toSet()) : null;
    }

}
