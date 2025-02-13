package pl.edu.agh.service.Result;

import org.bson.types.ObjectId;
import org.moeaframework.problem.misc.Lis;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import pl.edu.agh.dto.SingleRunExperimentResult;
import pl.edu.agh.model.Experiment;
import pl.edu.agh.model.ExperimentResult;
import pl.edu.agh.query.MongoQueryBuilder;
import pl.edu.agh.repository.ExperimentGroupRepository;
import pl.edu.agh.repository.ExperimentRepository;
import pl.edu.agh.repository.ExperimentResultRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExperimentResultService {
    private final ExperimentRepository experimentRepository;
    private final ExperimentResultRepository resultRepository;
    private final ExperimentGroupRepository groupRepository;

    public ExperimentResultService(
        ExperimentRepository experimentRepository,
        ExperimentResultRepository resultRepository,
        ExperimentGroupRepository groupRepository
    ) {
        this.experimentRepository = experimentRepository;
        this.resultRepository = resultRepository;
        this.groupRepository = groupRepository;
    }

    public Experiment getExperiment(ObjectId id) {
        return experimentRepository.findById(id).orElse(null);
    }

    public List<Experiment> listExperiments() {
        return experimentRepository.findAll();
    }

    public List<Experiment> listExperimentsWithFilters(List<FilterCriterion> filters, String groupName) {
        MongoQueryBuilder builder = new MongoQueryBuilder();
        for (FilterCriterion filter : filters) {
            filter.apply(builder);
        }

        Query query = builder.build();

        List<Experiment> experiments = experimentRepository.findByQuery(query);

        if (experiments.isEmpty()) {
            return List.of();
        }

        if (groupName != null) {
            var group = groupRepository.findByGroupName(groupName);

            if (group == null) {
                return List.of();
            }

            return group.getGroupedExperiments().stream()
                    .filter(experiments::contains)
                    .collect(Collectors.toList());
        }

        return experiments;
    }

    public List<SingleRunExperimentResult> getResults(ObjectId experimentId) {
        return resultRepository.findByExperimentId(experimentId).stream()
                .map(this::getSingleRunExperimentResult)
                .collect(Collectors.toList());
    }

    private SingleRunExperimentResult getSingleRunExperimentResult(ExperimentResult r) {
        return new SingleRunExperimentResult(
                r.getAlgorithm(),
                r.getProblem(),
                r.getRunCount(),
                r.getIterationResults()
        );
    }
}
