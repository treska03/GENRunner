package pl.edu.agh.controller;

import org.bson.types.ObjectId;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pl.edu.agh.controller.Validation.ValidAlgorithmList;
import pl.edu.agh.controller.Validation.ValidDate;
import pl.edu.agh.controller.Validation.ValidMetricsList;
import pl.edu.agh.controller.Validation.ValidProblemsList;
import pl.edu.agh.dto.*;
import pl.edu.agh.model.Experiment;
import pl.edu.agh.model.ExperimentStatus;
import pl.edu.agh.service.ExperimentService;
import pl.edu.agh.service.Result.FilterCriterion;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static pl.edu.agh.model.ExperimentFieldNames.*;

@RestController
@RequestMapping("/experiments")
public class ExperimentController {

    private final ExperimentService experimentService;

    public ExperimentController(ExperimentService experimentService) {
        this.experimentService = experimentService;
    }

    @PostMapping
    public ResponseEntity<CreateExperimentResponse> createExperiment(@RequestBody CreateExperimentRequest req) {
        if (req.algorithms() == null || req.problems() == null || req.metrics() == null || req.budget() == 0 || req.budget() % 100 != 0 || req.runs() <= 0) {
            return ResponseEntity.badRequest().build();
        }

        ObjectId id = experimentService.runExperiments(req.algorithms(), req.problems(), req.metrics(), req.budget(), req.runs());
        return ResponseEntity.ok(new CreateExperimentResponse(id.toHexString(), ExperimentStatus.PENDING));
    }

    @GetMapping
    public ResponseEntity<List<Experiment>> listExperiments() {
        return ResponseEntity.ok(experimentService.listExperiments());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteExperiment(@PathVariable("id") String idStr) {
        ObjectId id;
        try {
            id = new ObjectId(idStr);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid id format");
        }

        experimentService.deleteExperiment(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/filtered")
    public ResponseEntity<List<Experiment>> listExperimentsWithFilters(
            @RequestParam(required = false) List<String> algorithms,
            @RequestParam(required = false) List<String> problems,
            @RequestParam(required = false) List<String> metrics,
            @RequestParam(required = false) String groupName
    ) {

        List<FilterCriterion> filters = Arrays.asList(
                new FilterCriterion(algorithms, ALGORITHMS_ENUM),
                new FilterCriterion(problems, PROBLEMS_ENUM),
                new FilterCriterion(metrics, METRICS_ENUM)
        );
        List<Experiment> experiments = experimentService.listExperimentsWithFilters(filters, groupName);
        return ResponseEntity.ok(experiments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getExperimentDetails(@PathVariable("id") String idStr) {
        ObjectId id;
        try {
            id = new ObjectId(idStr);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid id format");
        }
        Experiment exp = experimentService.getExperiment(id);
        if (exp == null) {
            return ResponseEntity.badRequest().body("No Experiment with provided id");
        }
        return ResponseEntity.ok(exp);
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<ExperimentStatusResponse> getExperimentStatus(@PathVariable("id") String idStr) {
        ObjectId id = new ObjectId(idStr);
        Experiment exp = experimentService.getExperiment(id);
        return ResponseEntity.ok(new ExperimentStatusResponse(id.toHexString(), exp.getStatus(), exp.getErrorMessage()));
    }

    @GetMapping("/{id}/results")
    public ResponseEntity<ExperimentResultsResponse> getExperimentResults(@PathVariable("id") String idStr) {
        ObjectId id = new ObjectId(idStr);
        Experiment exp = experimentService.getExperiment(id);
        if (exp.getStatus() != ExperimentStatus.FINISHED) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }

        List<SingleRunExperimentResult> results = experimentService.getResults(id);

        ExperimentResultsResponse resp = new ExperimentResultsResponse(
                id.toHexString(),
                results
        );
        return ResponseEntity.ok(resp);
    }

    @Validated
    @GetMapping("/aggregate")
    public ResponseEntity<List<AggregatedExperimentDto>> aggregateFinishedExperimentsBetweenDates(
            @ValidDate @RequestParam(value = "startDate", required = false) String startDate,
            @ValidDate @RequestParam(value = "endDate", required = false) String endDate,
            @ValidAlgorithmList @RequestParam(value = "algorithms", required = false) List<String> algorithms,
            @ValidProblemsList @RequestParam(value = "problems", required = false) List<String> problems,
            @ValidMetricsList @RequestParam(value = "metrics", required = false) List<String> metrics,
            @RequestParam(value = "groupName" , required = false) String groupName
    ) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneId.of("UTC"));

        Instant parsedStartDate = null;
        Instant parsedEndDate = null;

        if (startDate != null) {
            parsedStartDate = Instant.from(formatter.parse(startDate));
        }
        if (endDate != null) {
            parsedEndDate = Instant.from(formatter.parse(endDate));
        }

        List<AggregatedExperimentDto> aggregatedResults =
                experimentService.aggregateExperiments(parsedStartDate, parsedEndDate, algorithms, problems, metrics, groupName);

        return ResponseEntity.ok(aggregatedResults);
    }

    @Validated
    @GetMapping("/aggregate/csv")
    public ResponseEntity<byte[]> aggregateExperimentsToCsv(
            @ValidDate @RequestParam(value = "startDate", required = false) String startDate,
            @ValidDate @RequestParam(value = "endDate", required = false) String endDate,
            @ValidAlgorithmList @RequestParam(value = "algorithms", required = false) List<String> algorithms,
            @ValidProblemsList @RequestParam(value = "problems", required = false) List<String> problems,
            @ValidMetricsList @RequestParam(value = "metrics", required = false) List<String> metrics,
            @RequestParam(value = "groupName" , required = false) String groupName
    ) {
        String csvContent = experimentService.generateCsvContent(startDate, endDate, algorithms, problems, metrics, groupName);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"aggregated_results.csv\"")
                .contentType(MediaType.TEXT_PLAIN)
                .body(csvContent.getBytes());
    }

    @Validated
    @GetMapping("/aggregate/chart")
    public ResponseEntity<byte[]> aggregateExperimentsToChart(
            @ValidDate @RequestParam(value = "startDate", required = false) String startDate,
            @ValidDate @RequestParam(value = "endDate", required = false) String endDate,
            @ValidAlgorithmList @RequestParam(value = "algorithms", required = false) List<String> algorithms,
            @ValidProblemsList @RequestParam(value = "problems", required = false) List<String> problems,
            @ValidMetricsList @RequestParam(value = "metrics", required = false) List<String> metrics,
            @RequestParam(value = "groupName" , required = false) String groupName
    ) throws IOException {
        byte[] chartContent = experimentService.generateChartContent(startDate, endDate, algorithms, problems, metrics, groupName);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"aggregated_results.png\"")
                .contentType(MediaType.IMAGE_PNG)
                .body(chartContent);
    }

    @PostMapping("/group")
    public ResponseEntity<String> groupExperiments(
            @RequestBody GroupedExperimentsRequest groupRequest
    ){
        experimentService.groupExperiments(groupRequest.groupName(),groupRequest.experimentsIdToAdd());

        return ResponseEntity.ok(groupRequest.groupName());

    }

    @GetMapping("/group/{groupName}")
    public ResponseEntity<?> getGroupExperiments(@PathVariable("groupName") String groupName){
        //TODO finish
        return ResponseEntity.ok(experimentService.getGroupedExperiments(groupName));
    }

    @DeleteMapping("/group/{groupName}")
    public ResponseEntity<?> deleteExperimentsGroup(@PathVariable("groupName") String groupName){
        experimentService.deleteExperimentsGroup(groupName);
        return ResponseEntity.ok().build();
    }
}

