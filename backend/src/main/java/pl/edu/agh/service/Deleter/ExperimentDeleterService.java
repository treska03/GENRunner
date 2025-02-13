package pl.edu.agh.service.Deleter;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import pl.edu.agh.exception.ExperimentGroupNotFoundException;
import pl.edu.agh.exception.ExperimentNotFoundException;
import pl.edu.agh.model.Experiment;
import pl.edu.agh.model.ExperimentGroup;
import pl.edu.agh.repository.ExperimentGroupRepository;
import pl.edu.agh.repository.ExperimentRepository;
import pl.edu.agh.repository.ExperimentResultRepository;
import pl.edu.agh.repository.MongoTransactionRunner;

@Service
public class ExperimentDeleterService {

    private ExperimentRepository experimentRepository;
    private ExperimentResultRepository resultRepository;
    private ExperimentGroupRepository groupRepository;
    private MongoTransactionRunner transactionRunner;

    public ExperimentDeleterService(ExperimentRepository experimentRepository, ExperimentResultRepository resultRepository, ExperimentGroupRepository groupRepository, MongoTransactionRunner transactionRunner) {
        this.experimentRepository = experimentRepository;
        this.resultRepository = resultRepository;
        this.groupRepository = groupRepository;
        this.transactionRunner = transactionRunner;
    }

    public void deleteExperimentGroup(String groupName) {
        ExperimentGroup group = groupRepository.findByGroupName(groupName);
        if(group == null) {
            throw new ExperimentGroupNotFoundException(groupName);
        }
        transactionRunner.runWithTransaction(() -> deleteGroup(group));
    }

    private void deleteGroup(ExperimentGroup group) {
        group.getGroupedExperiments().forEach(exp -> deleteExperiment(exp.getId()));
        groupRepository.deleteById(group.getId());
    }

    public void deleteExperiment(ObjectId id) {
        Experiment experiment = experimentRepository.findById(id).orElseThrow(() -> new ExperimentNotFoundException(id));
        transactionRunner.runWithTransaction(() -> deleteExperiment(experiment));
    }

    private void deleteExperiment(Experiment experiment) {
        experimentRepository.delete(experiment);
        resultRepository.deleteAllByExperimentId(experiment.getId());
        removeExperimentFromAllGroups(experiment);
    }

    private void removeExperimentFromAllGroups(Experiment experiment) {
        groupRepository.findByGroupedExperimentId(experiment.getId()).forEach(group -> {
            group.getGroupedExperiments().remove(experiment);
            groupRepository.save(group);
        });
    }
}
