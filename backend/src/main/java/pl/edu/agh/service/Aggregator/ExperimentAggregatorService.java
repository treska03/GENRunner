package pl.edu.agh.service.Aggregator;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import pl.edu.agh.dto.AggregatedExperimentDto;
import pl.edu.agh.dto.IterationResult;
import pl.edu.agh.model.Experiment;
import pl.edu.agh.model.ExperimentResult;
import pl.edu.agh.query.MongoQueryBuilder;
import pl.edu.agh.query.Operation;
import pl.edu.agh.repository.ExperimentGroupRepository;
import pl.edu.agh.repository.ExperimentRepository;
import pl.edu.agh.repository.ExperimentResultRepository;
import pl.edu.agh.service.Aggregator.Statistic.*;
import pl.edu.agh.service.Aggregator.Util.AllDirectAlgProbResults;
import pl.edu.agh.service.Aggregator.Util.MetricResults;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static pl.edu.agh.model.Experiment.FieldNames.*;

@Service
public class ExperimentAggregatorService {

    private final ExperimentRepository experimentRepository;
    private final ExperimentResultRepository resultRepository;
    private final ExperimentGroupRepository groupRepository;

    public ExperimentAggregatorService(
            ExperimentRepository experimentRepository,
            ExperimentResultRepository resultRepository,
            ExperimentGroupRepository groupRepository
    ) {
        this.experimentRepository = experimentRepository;
        this.resultRepository = resultRepository;
        this.groupRepository = groupRepository;
    }

    public static void sortResultDtos(List<AggregatedExperimentDto> resultDtos) {
        resultDtos.sort(
                Comparator.comparing(AggregatedExperimentDto::algorithm)
                        .thenComparing(AggregatedExperimentDto::problem)
                        .thenComparing(AggregatedExperimentDto::indicator)
                        .thenComparingInt(AggregatedExperimentDto::iteration)
        );
    }

    public List<ExperimentResult> fetchExperimentResults(
            Instant startDate,
            Instant endDate,
            List<String> algorithms,
            List<String> problems,
            List<String> metrics,
            String groupName
    ) {
        Query queryBuilder = new MongoQueryBuilder().addFilter(ALGORITHMS, Operation.IN, algorithms)
                .addFilter(PROBLEMS, Operation.IN, problems)
                .addFilter(METRICS, Operation.IN, metrics)
                .addFilter(STATUS, Operation.EQ, "FINISHED")
                .addFilter(START_TIME, Operation.GTE, startDate)
                .addFilter(END_TIME, Operation.LTE, endDate)
                .build();

        List<Experiment> experiments = experimentRepository.findByQuery(queryBuilder);

        if (experiments.isEmpty()) {
            return List.of();
        }

        if (groupName != null) {
            var group = groupRepository.findByGroupName(groupName);

            if (group == null) {
                return List.of();
            }

            Set<ObjectId> groupedExperimentIds = group.getGroupedExperiments().stream()
                    .map(Experiment::getId)
                    .collect(Collectors.toSet());

            experiments = experiments.stream()
                    .filter(experiment -> groupedExperimentIds.contains(experiment.getId()))
                    .toList();
        }

        List<ObjectId> experimentIds = experiments.stream()
                .map(Experiment::getId)
                .collect(Collectors.toList());

        return resultRepository.findByExperimentIdIn(experimentIds).stream().toList();
    }

    public Map<String, AllDirectAlgProbResults> groupAndAggregateIterationResults(List<ExperimentResult> experimentResults) {
        Map<String, AllDirectAlgProbResults> aggregationMap = new HashMap<>();

        for (ExperimentResult experiment : experimentResults) {
            String algProbKey = experiment.getAlgorithm() + "#" + experiment.getProblem();

            for (IterationResult iterationResult : experiment.getIterationResults()) {
                AllDirectAlgProbResults algProbResults = aggregationMap.computeIfAbsent(
                        algProbKey,
                        key -> new AllDirectAlgProbResults(experiment.getAlgorithm(), experiment.getProblem())
                );

                algProbResults.addIterationResult(iterationResult);
            }
        }

        aggregationMap.values().forEach(AllDirectAlgProbResults::compute);
        return aggregationMap;
    }

    public List<AggregatedExperimentDto> countStatsAndConvertToDto(Map<String, AllDirectAlgProbResults> algProbResultsMap) {
        List<AggregatedExperimentDto> resultDtos = new ArrayList<>();
        StatsContext statsContext = new StatsContext(List.of(new AverageComputation(), new MedianComputation(), new STDComputation()));
        for (AllDirectAlgProbResults algProbResults : algProbResultsMap.values()) {
            for (MetricResults metricResults : algProbResults.getAllMetricResults().values()) {
                AggregationStats stats = statsContext.aggregate(metricResults.getResultValues());

                AggregatedExperimentDto dto = new AggregatedExperimentDto(
                        algProbResults.getAlgorithm(),
                        algProbResults.getProblem(),
                        metricResults.getIteration(),
                        metricResults.getMetric().toString(),
                        stats.getMean(),
                        stats.getMedian(),
                        stats.getStandardDeviation(),
                        stats.getCount()
                );
                resultDtos.add(dto);
            }
        }

        sortResultDtos(resultDtos);
        return resultDtos;
    }

    public List<AggregatedExperimentDto> aggregateExperiments(
            Instant startDate,
            Instant endDate,
            List<String> algorithms,
            List<String> problems,
            List<String> metrics,
            String groupName
    ) {
        List<ExperimentResult> experimentResults = fetchExperimentResults(
                startDate, endDate, algorithms, problems, metrics, groupName
        );

        Map<String, AllDirectAlgProbResults> aggregatedResults = groupAndAggregateIterationResults(experimentResults);

        return countStatsAndConvertToDto(aggregatedResults);
    }
}

