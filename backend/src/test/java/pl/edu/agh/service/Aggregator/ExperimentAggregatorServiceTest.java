package pl.edu.agh.service.Aggregator;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.moeaframework.core.indicator.StandardIndicator;
import pl.edu.agh.dto.AggregatedExperimentDto;
import pl.edu.agh.dto.IterationResult;
import pl.edu.agh.model.Experiment;
import pl.edu.agh.model.ExperimentResult;
import pl.edu.agh.model.ExperimentStatus;
import pl.edu.agh.repository.ExperimentRepository;
import pl.edu.agh.repository.ExperimentResultRepository;
import pl.edu.agh.service.Aggregator.Util.AllDirectAlgProbResults;

import java.time.Instant;
import java.util.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Criteria;

@ExtendWith(MockitoExtension.class)
public class ExperimentAggregatorServiceTest {
    @Mock
    private ExperimentRepository experimentRepository;

    @Mock
    private ExperimentResultRepository resultRepository;

    @InjectMocks
    private ExperimentAggregatorService aggregatorService;

    private Experiment experiment;
    private ExperimentResult experimentResult;

    @BeforeEach
    public void setUp() {
        experiment = new Experiment();
        experiment.setId(new ObjectId("507f1f77bcf86cd799439011"));
        experiment.setAlgorithms(Set.of("VEGA"));
        experiment.setProblems(Set.of("UF1"));
        experiment.setMetrics(Set.of(StandardIndicator.Hypervolume));
        experiment.setStatus(ExperimentStatus.FINISHED);
        experiment.setStartTime(Instant.parse("2023-01-01T00:00:00Z"));
        experiment.setEndTime(Instant.parse("2023-01-02T00:00:00Z"));

        experimentResult = new ExperimentResult();
        experimentResult.setExperiment(experiment);
        experimentResult.setAlgorithm("VEGA");
        experimentResult.setProblem("UF1");
        experimentResult.setIterationResults(List.of(new IterationResult(1, Map.of(StandardIndicator.Hypervolume, 0.5))));
    }

    @Test
    public void testFetchExperimentResults() {
        Instant startDate = Instant.parse("2023-01-01T00:00:00Z");
        Instant endDate = Instant.parse("2023-01-02T00:00:00Z");
        List<String> algorithms = List.of("VEGA");
        List<String> problems = List.of("UF1");
        List<String> metrics = List.of("Hypervolume");

        // Create the expected query object
        Query query = new Query();
        query.addCriteria(Criteria.where("algorithms").in(algorithms));
        query.addCriteria(Criteria.where("problems").in(problems));
        query.addCriteria(Criteria.where("metrics").in(metrics));
        query.addCriteria(Criteria.where("status").is("FINISHED"));
        query.addCriteria(Criteria.where("start_time").gte(startDate));
        query.addCriteria(Criteria.where("end_time").lte(endDate));

        when(experimentRepository.findByQuery(ArgumentMatchers.any(Query.class))).thenReturn(List.of(experiment));
        //when(resultRepository.findByExperimentId(new ObjectId("507f1f77bcf86cd799439011"))).thenReturn(List.of(experimentResult));
        when(resultRepository.findByExperimentIdIn(ArgumentMatchers.anyList())).thenReturn(Set.of(experimentResult));

        List<ExperimentResult> results = aggregatorService.fetchExperimentResults(startDate, endDate, algorithms, problems, metrics, null);

        assertEquals(1, results.size());
        assertEquals(experimentResult, results.get(0));
    }

    @Test
    public void testGroupAndAggregateIterationResults() {
        List<ExperimentResult> experimentResults = List.of(experimentResult);

        Map<String, AllDirectAlgProbResults> result = aggregatorService.groupAndAggregateIterationResults(experimentResults);

        assertEquals(1, result.size());
        assertEquals("VEGA#UF1", result.keySet().iterator().next());
    }

    @Test
    public void testCountStatsAndConvertToDto() {
        AllDirectAlgProbResults allDirectAlgProbResults = new AllDirectAlgProbResults("VEGA", "UF1");
        allDirectAlgProbResults.addIterationResult(new IterationResult(1, Map.of(StandardIndicator.Hypervolume, 0.5)));
        allDirectAlgProbResults.compute();

        Map<String, AllDirectAlgProbResults> algProbResultsMap = Map.of("VEGA#UF1", allDirectAlgProbResults);

        List<AggregatedExperimentDto> resultDtos = aggregatorService.countStatsAndConvertToDto(algProbResultsMap);

        assertEquals(1, resultDtos.size());
        AggregatedExperimentDto dto = resultDtos.get(0);
        assertEquals("VEGA", dto.algorithm());
        assertEquals("UF1", dto.problem());
        assertEquals("Hypervolume", dto.indicator());
        assertEquals(1, dto.iteration());
        assertEquals(0.5, dto.average());
    }

    @Test
    public void testAggregateExperiments() {
        Instant startDate = Instant.parse("2023-01-01T00:00:00Z");
        Instant endDate = Instant.parse("2023-01-02T00:00:00Z");
        List<String> algorithms = List.of("VEGA");
        List<String> problems = List.of("UF1");
        List<String> metrics = List.of("Hypervolume");

        when(experimentRepository.findByQuery(ArgumentMatchers.any(Query.class))).thenReturn(List.of(experiment));
        //when(resultRepository.findByExperimentId(new ObjectId("507f1f77bcf86cd799439011"))).thenReturn(List.of(experimentResult));
        when(resultRepository.findByExperimentIdIn(ArgumentMatchers.anyList())).thenReturn(Set.of(experimentResult));

        List<AggregatedExperimentDto> resultDtos = aggregatorService.aggregateExperiments(startDate, endDate, algorithms, problems, metrics, null);

        assertEquals(1, resultDtos.size());
        AggregatedExperimentDto dto = resultDtos.get(0);
        assertEquals("VEGA", dto.algorithm());
        assertEquals("UF1", dto.problem());
        assertEquals("Hypervolume", dto.indicator());
        assertEquals(1, dto.iteration());
        assertEquals(0.5, dto.average());
    }

    @Test
    public void testFetchExperimentResultsWithNoResults() {
        Instant startDate = Instant.parse("2023-01-01T00:00:00Z");
        Instant endDate = Instant.parse("2023-01-02T00:00:00Z");
        List<String> algorithms = List.of("VEGA");
        List<String> problems = List.of("UF1");
        List<String> metrics = List.of("Hypervolume");

        when(experimentRepository.findByQuery(ArgumentMatchers.any(Query.class))).thenReturn(Collections.emptyList());

        List<ExperimentResult> results = aggregatorService.fetchExperimentResults(startDate, endDate, algorithms, problems, metrics, null);

        assertEquals(0, results.size());
    }

    @Test
    public void testGroupAndAggregateIterationResultsWithNoResults() {
        List<ExperimentResult> experimentResults = Collections.emptyList();

        Map<String, AllDirectAlgProbResults> result = aggregatorService.groupAndAggregateIterationResults(experimentResults);

        assertEquals(0, result.size());
    }

    @Test
    public void testCountStatsAndConvertToDtoWithNoResults() {
        Map<String, AllDirectAlgProbResults> algProbResultsMap = Collections.emptyMap();

        List<AggregatedExperimentDto> resultDtos = aggregatorService.countStatsAndConvertToDto(algProbResultsMap);

        assertEquals(0, resultDtos.size());
    }

    @Test
    public void testAggregateExperimentsWithNoResults() {
        Instant startDate = Instant.parse("2023-01-01T00:00:00Z");
        Instant endDate = Instant.parse("2023-01-02T00:00:00Z");
        List<String> algorithms = List.of("VEGA");
        List<String> problems = List.of("UF1");
        List<String> metrics = List.of("Hypervolume");

        when(experimentRepository.findByQuery(ArgumentMatchers.any(Query.class))).thenReturn(Collections.emptyList());

        List<AggregatedExperimentDto> resultDtos = aggregatorService.aggregateExperiments(startDate, endDate, algorithms, problems, metrics, null);

        assertEquals(0, resultDtos.size());
    }

    @Test
    public void testFetchExperimentResultsWithMultipleAlgorithmsAndProblems() {
        Instant startDate = Instant.parse("2023-01-01T00:00:00Z");
        Instant endDate = Instant.parse("2023-01-02T00:00:00Z");
        List<String> algorithms = List.of("VEGA", "NSGA-II");
        List<String> problems = List.of("UF1", "UF2");
        List<String> metrics = List.of("Hypervolume", "Spread");

        when(experimentRepository.findByQuery(ArgumentMatchers.any(Query.class))).thenReturn(List.of(experiment));
        //when(resultRepository.findByExperimentId(new ObjectId("507f1f77bcf86cd799439011"))).thenReturn(List.of(experimentResult));
        when(resultRepository.findByExperimentIdIn(ArgumentMatchers.anyList())).thenReturn(Set.of(experimentResult));

        List<ExperimentResult> results = aggregatorService.fetchExperimentResults(startDate, endDate, algorithms, problems, metrics, null);

        assertEquals(1, results.size());
        assertEquals(experimentResult, results.get(0));
    }

    @Test
    public void testGroupAndAggregateIterationResultsWithMultipleAlgorithmsAndProblems() {
        ExperimentResult experimentResult2 = new ExperimentResult();
        experimentResult2.setExperiment(experiment);
        experimentResult2.setAlgorithm("NSGA-II");
        experimentResult2.setProblem("UF2");
        experimentResult2.setIterationResults(List.of(new IterationResult(1, Map.of(StandardIndicator.GenerationalDistance, 0.3))));

        List<ExperimentResult> experimentResults = List.of(experimentResult, experimentResult2);

        Map<String, AllDirectAlgProbResults> result = aggregatorService.groupAndAggregateIterationResults(experimentResults);

        assertEquals(2, result.size());
        assertTrue(result.containsKey("VEGA#UF1"));
        assertTrue(result.containsKey("NSGA-II#UF2"));
    }

    @Test
    public void testCountStatsAndConvertToDtoWithMultipleMetrics() {
        AllDirectAlgProbResults allDirectAlgProbResults = new AllDirectAlgProbResults("VEGA", "UF1");
        allDirectAlgProbResults.addIterationResult(new IterationResult(1, Map.of(StandardIndicator.Hypervolume, 0.5, StandardIndicator.GenerationalDistance, 0.3)));
        allDirectAlgProbResults.compute();

        Map<String, AllDirectAlgProbResults> algProbResultsMap = Map.of("VEGA#UF1", allDirectAlgProbResults);

        List<AggregatedExperimentDto> resultDtos = aggregatorService.countStatsAndConvertToDto(algProbResultsMap);

        assertEquals(2, resultDtos.size());
        AggregatedExperimentDto dto1 = resultDtos.get(0);
        assertEquals("VEGA", dto1.algorithm());
        assertEquals("UF1", dto1.problem());
        assertEquals("GenerationalDistance", dto1.indicator());
        assertEquals(1, dto1.iteration());
        assertEquals(0.3, dto1.average());

        AggregatedExperimentDto dto2 = resultDtos.get(1);
        assertEquals("VEGA", dto2.algorithm());
        assertEquals("UF1", dto2.problem());
        assertEquals("Hypervolume", dto2.indicator());
        assertEquals(1, dto2.iteration());
        assertEquals(0.5, dto2.average());
    }

    @Test
    public void testFetchExperimentResultsWithNoMatchingExperiments() {
        Instant startDate = Instant.parse("2023-01-01T00:00:00Z");
        Instant endDate = Instant.parse("2023-01-02T00:00:00Z");
        List<String> algorithms = List.of("VEGA");
        List<String> problems = List.of("UF1");
        List<String> metrics = List.of("Hypervolume");

        when(experimentRepository.findByQuery(ArgumentMatchers.any(Query.class))).thenReturn(Collections.emptyList());

        List<ExperimentResult> results = aggregatorService.fetchExperimentResults(startDate, endDate, algorithms, problems, metrics, null);

        assertEquals(0, results.size());
    }

    @Test
    public void testGroupAndAggregateIterationResultsWithMultipleIterations() {
        IterationResult iterationResult1 = new IterationResult(1, Map.of(StandardIndicator.Hypervolume, 0.5));
        IterationResult iterationResult2 = new IterationResult(2, Map.of(StandardIndicator.Hypervolume, 0.6));
        experimentResult.setIterationResults(List.of(iterationResult1, iterationResult2));

        List<ExperimentResult> experimentResults = List.of(experimentResult);

        Map<String, AllDirectAlgProbResults> result = aggregatorService.groupAndAggregateIterationResults(experimentResults);

        assertEquals(1, result.size());
        AllDirectAlgProbResults aggregatedResults = result.get("VEGA#UF1");
        assertEquals(1, aggregatedResults.getAllMetricResults().get("Hypervolume#1").getResultValues().size());
        assertEquals(1, aggregatedResults.getAllMetricResults().get("Hypervolume#2").getResultValues().size());
    }

    @Test
    public void testCountStatsAndConvertToDtoWithMultipleIterationsAndMetrics() {
        AllDirectAlgProbResults allDirectAlgProbResults = new AllDirectAlgProbResults("VEGA", "UF1");
        allDirectAlgProbResults.addIterationResult(new IterationResult(1, Map.of(StandardIndicator.Hypervolume, 0.5, StandardIndicator.GenerationalDistance, 0.3)));
        allDirectAlgProbResults.addIterationResult(new IterationResult(2, Map.of(StandardIndicator.Hypervolume, 0.6, StandardIndicator.GenerationalDistance, 0.4)));
        allDirectAlgProbResults.compute();

        Map<String, AllDirectAlgProbResults> algProbResultsMap = Map.of("VEGA#UF1", allDirectAlgProbResults);

        List<AggregatedExperimentDto> resultDtos = aggregatorService.countStatsAndConvertToDto(algProbResultsMap);

        assertEquals(4, resultDtos.size());
        AggregatedExperimentDto dto1 = resultDtos.get(0);
        assertEquals("VEGA", dto1.algorithm());
        assertEquals("UF1", dto1.problem());
        assertEquals("GenerationalDistance", dto1.indicator());
        assertEquals(1, dto1.iteration());
        assertEquals(0.3, dto1.average());

        AggregatedExperimentDto dto2 = resultDtos.get(1);
        assertEquals("VEGA", dto2.algorithm());
        assertEquals("UF1", dto2.problem());
        assertEquals("GenerationalDistance", dto2.indicator());
        assertEquals(2, dto2.iteration());
        assertEquals(0.4, dto2.average());

        AggregatedExperimentDto dto3 = resultDtos.get(2);
        assertEquals("VEGA", dto3.algorithm());
        assertEquals("UF1", dto3.problem());
        assertEquals("Hypervolume", dto3.indicator());
        assertEquals(1, dto3.iteration());
        assertEquals(0.5, dto3.average());

        AggregatedExperimentDto dto4 = resultDtos.get(3);
        assertEquals("VEGA", dto4.algorithm());
        assertEquals("UF1", dto4.problem());
        assertEquals("Hypervolume", dto4.indicator());
        assertEquals(2, dto4.iteration());
        assertEquals(0.6, dto4.average());
    }
}