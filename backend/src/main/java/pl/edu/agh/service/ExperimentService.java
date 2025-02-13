package pl.edu.agh.service;

import org.bson.types.ObjectId;
import org.moeaframework.core.indicator.StandardIndicator;
import org.springframework.stereotype.Service;
import pl.edu.agh.dto.AggregatedExperimentDto;
import pl.edu.agh.dto.SingleRunExperimentResult;
import pl.edu.agh.model.Experiment;
import pl.edu.agh.service.Aggregator.ExperimentAggregatorService;
import pl.edu.agh.service.Deleter.ExperimentDeleterService;
import pl.edu.agh.service.Generator.CSVGenerator;
import pl.edu.agh.service.Generator.ChartGenerator;
import pl.edu.agh.service.Group.ExperimentGroupingService;
import pl.edu.agh.service.Result.ExperimentResultService;
import pl.edu.agh.service.Result.FilterCriterion;
import pl.edu.agh.service.Runner.ExperimentRunnerService;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

@Service
public class ExperimentService {

    private final ExperimentRunnerService runnerService;
    private final ExperimentAggregatorService aggregatorService;
    private final ExperimentResultService resultService;
    private final ExperimentDeleterService deleterService;

    private final ExperimentGroupingService groupingService;

    private final CSVGenerator csvGenerator;
    private final ChartGenerator chartGenerator;


    public ExperimentService(ExperimentRunnerService runnerService, ExperimentAggregatorService aggregatorService, ExperimentResultService resultService, ExperimentDeleterService deleterService, ExperimentGroupingService groupingService) {
        this.runnerService = runnerService;
        this.aggregatorService = aggregatorService;
        this.resultService = resultService;
        this.deleterService = deleterService;
        this.groupingService = groupingService;
        this.csvGenerator = new CSVGenerator();
        this.chartGenerator = new ChartGenerator();
    }

    public List<AggregatedExperimentDto> aggregateExperiments(Instant startDate, Instant endDate,
                                                              List<String> algorithms,
                                                              List<String> problems,
                                                              List<String> metrics,
                                                              String groupName) {
        return aggregatorService.aggregateExperiments(startDate, endDate, algorithms, problems, metrics, groupName);
    }


    public ObjectId runExperiments(Set<String> algorithms, Set<String> problems, Set<StandardIndicator> metrics, int budget, int runs) {
        return runnerService.runExperiments(algorithms, problems, metrics, budget, runs);
    }

    public Experiment getExperiment(ObjectId id) {
        return resultService.getExperiment(id);
    }

    public List<Experiment> listExperiments() {
        return resultService.listExperiments();
    }

    public List<Experiment> listExperimentsWithFilters(List<FilterCriterion> filters, String groupName) {
        return resultService.listExperimentsWithFilters(filters, groupName);
    }

    public List<SingleRunExperimentResult> getResults(ObjectId experimentId) {
        return resultService.getResults(experimentId);
    }

    public void deleteExperiment(ObjectId experimentId) {
        deleterService.deleteExperiment(experimentId);
    }

    //TODO change returned type
    public void groupExperiments(String groupName, List<String> experimentsIdToAdd){
        groupingService.groupExperiments(groupName,experimentsIdToAdd);
    }

    public List<Experiment> getGroupedExperiments(String groupName){
        return groupingService.getGroupedExperiments(groupName);
    }

    public void deleteExperimentsGroup(String groupName) {
        deleterService.deleteExperimentGroup(groupName);
    }

    public List<AggregatedExperimentDto> getAggregatedResults(
            String startDate,
            String endDate,
            List<String> algorithms,
            List<String> problems,
            List<String> metrics,
            String groupName
    ) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("UTC"));
        Instant parsedStartDate = startDate != null ? Instant.from(formatter.parse(startDate)) : null;
        Instant parsedEndDate = endDate != null ? Instant.from(formatter.parse(endDate)) : null;

        return aggregateExperiments(parsedStartDate, parsedEndDate, algorithms, problems, metrics, groupName);
    }

    public String generateCsvContent(
            String startDate,
            String endDate,
            List<String> algorithms,
            List<String> problems,
            List<String> metrics,
            String groupName
    ) {
        List<AggregatedExperimentDto> aggregatedResults = getAggregatedResults(
                startDate, endDate, algorithms, problems, metrics, groupName
        );

        return csvGenerator.generateCsvContent(aggregatedResults);
    }

    public byte[] generateChartContent(
            String startDate,
            String endDate,
            List<String> algorithms,
            List<String> problems,
            List<String> metrics,
            String groupName
    ) throws IOException {
        List<AggregatedExperimentDto> aggregatedResults = getAggregatedResults(
                startDate, endDate, algorithms, problems, metrics, groupName
        );

        return chartGenerator.generateChartContent(aggregatedResults);
    }
}