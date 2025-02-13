package cli.api;

import cli.dto.AggregatedResultsDto;
import cli.dto.ExperimentResponseDto;
import cli.dto.ExperimentResultsResponseDto;
import cli.dto.ExperimentStatusResponseDto;
import org.nocrala.tools.texttablefmt.BorderStyle;
import org.nocrala.tools.texttablefmt.Table;
import org.nocrala.tools.texttablefmt.ShownBorders;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ResponseFormatter {

    private static final String ID_HEADER = "ID";
    private static final String ALGORITHMS_HEADER = "Algorithms";
    private static final String PROBLEMS_HEADER = "Problems";
    private static final String METRICS_HEADER = "Metrics";
    private static final String BUDGET_HEADER = "Budget";
    private static final String RUNS_HEADER = "Runs";
    private static final String STATUS_HEADER = "Status";
    private static final String START_TIME_HEADER = "Start Time";
    private static final String END_TIME_HEADER = "End Time";
    private static final String ERROR_MESSAGE_HEADER = "Error Message";
    private static final String ITERATION_HEADER = "Iteration";

    private static final String ALGORITHM_HEADER = "Algorithm";
    private static final String PROBLEM_HEADER = "Problem";
    private static final String RUN_COUNT_HEADER = "Run number";

    public String formatListResponse(List<ExperimentResponseDto> experiments) {
        Table table = new Table(10, BorderStyle.CLASSIC, ShownBorders.ALL);
        table.addCell(ID_HEADER);
        table.addCell(ALGORITHMS_HEADER);
        table.addCell(PROBLEMS_HEADER);
        table.addCell(METRICS_HEADER);
        table.addCell(BUDGET_HEADER);
        table.addCell(RUNS_HEADER);
        table.addCell(STATUS_HEADER);
        table.addCell(START_TIME_HEADER);
        table.addCell(END_TIME_HEADER);
        table.addCell(ERROR_MESSAGE_HEADER);
        for (ExperimentResponseDto experiment : experiments) {
            table.addCell(String.valueOf(experiment.id()));
            table.addCell(String.join(",", experiment.algorithms()));
            table.addCell(String.join(",", experiment.problems()));
            table.addCell(String.join(",", experiment.metrics()));
            table.addCell(String.valueOf(experiment.budget()));
            table.addCell(String.valueOf(experiment.runs()));
            table.addCell(experiment.status());
            table.addCell(experiment.startTime());
            table.addCell(experiment.endTime() != null ? experiment.endTime() : "null");
            table.addCell(experiment.errorMessage() != null ? experiment.errorMessage() : "None");
        }

        return table.render();
    }

    public String formatStatusResponse(ExperimentStatusResponseDto response) {
        return ID_HEADER + ": " + response.experimentId() + "\n" +
                STATUS_HEADER + ": " + response.status() + "\n" +
                ERROR_MESSAGE_HEADER + ": " + (response.errorMessage() != null ? response.errorMessage() : "None");
    }

    public String formatResultsResponse(ExperimentResultsResponseDto response) {
        StringBuilder sb = new StringBuilder();
        sb.append(ID_HEADER).append(": ").append(response.experimentId()).append("\n");

        for (ExperimentResultsResponseDto.AlgorithmResult result : response.results()) {
            sb.append(ALGORITHM_HEADER).append(": ").append(result.algorithm()).append("\n");
            sb.append(PROBLEM_HEADER).append(": ").append(result.problem()).append("\n");
            sb.append(RUN_COUNT_HEADER).append(": ").append(result.runCount()).append("\n");

            Table table = new Table(result.iterations().get(0).metricsResults().size() + 1, BorderStyle.CLASSIC, ShownBorders.ALL);
            table.addCell(ITERATION_HEADER);
            result.iterations().get(0).metricsResults().keySet().forEach(table::addCell);

            for (ExperimentResultsResponseDto.IterationResult iteration : result.iterations()) {
                table.addCell(String.valueOf(iteration.iteration()));
                iteration.metricsResults().values().forEach(value -> table.addCell(String.valueOf(value)));
            }

            sb.append(table.render()).append("\n");
        }

        return sb.toString();
    }

    public String formatAggregatedResultsResponse(List<AggregatedResultsDto> aggregates) {
        StringBuilder sb = new StringBuilder();
        sb.append("===== Aggregated Results =====\n\n");

        Map<String, List<AggregatedResultsDto>> groupedResults = aggregates.stream()
                .collect(Collectors.groupingBy(result -> result.algorithm() + "|" + result.problem() + "|" + result.indicator()));

        for (Map.Entry<String, List<AggregatedResultsDto>> entry : groupedResults.entrySet()) {
            String[] keys = entry.getKey().split("\\|");
            String algorithm = keys[0];
            String problem = keys[1];
            String indicator = keys[2];

            sb.append("Algorithm: ").append(algorithm)
                    .append(", Problem: ").append(problem)
                    .append(", Indicator: ").append(indicator)
                    .append("\n");

            Table table = new Table(5, BorderStyle.CLASSIC, ShownBorders.ALL);
            table.addCell("Iteration");
            table.addCell("Average");
            table.addCell("Median");
            table.addCell("StdDev");
            table.addCell("Count");

            for (AggregatedResultsDto result : entry.getValue()) {
                table.addCell(String.valueOf(result.iteration()));
                table.addCell(String.valueOf(result.average()));
                table.addCell(String.valueOf(result.median()));
                table.addCell(String.valueOf(result.standardDeviation()));
                table.addCell(String.valueOf(result.count()));
            }

            sb.append(table.render()).append("\n\n");
        }

        return sb.toString();
    }

}