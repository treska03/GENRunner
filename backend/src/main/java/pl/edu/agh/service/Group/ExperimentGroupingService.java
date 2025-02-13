package pl.edu.agh.service.Group;

import org.bson.types.ObjectId;
import org.moeaframework.problem.misc.Lis;
import org.springframework.stereotype.Service;
import pl.edu.agh.model.Experiment;
import pl.edu.agh.model.ExperimentGroup;
import pl.edu.agh.repository.ExperimentGroupRepository;
import pl.edu.agh.repository.ExperimentRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ExperimentGroupingService {

    private final ExperimentRepository experimentRepository;

    private final ExperimentGroupRepository groupRepository;

    public ExperimentGroupingService(ExperimentRepository experimentRepository, ExperimentGroupRepository groupRepository) {
        this.experimentRepository = experimentRepository;
        this.groupRepository = groupRepository;
    }

    //TODO nie mam pomysłu jak z poziomu kontrolera sensownie mówić dodaj tak przefiltrowane dane
    public void groupExperiments(String groupName, List<String> experimentsIdToAdd) {
        ExperimentGroup experimentGroup = groupRepository.findByGroupName(groupName);

        if(experimentGroup == null){
            experimentGroup = new ExperimentGroup();
            experimentGroup.setGroupName(groupName);
            experimentGroup.setGroupedExperiments(new HashSet<>());
        }

        if (experimentsIdToAdd != null){
            Set<Experiment> fetchedExperiments = experimentRepository.findExperimentsByIdIn(experimentsIdToAdd.stream().map(ObjectId::new)
                    .collect(Collectors.toList()));
            experimentGroup.getGroupedExperiments().addAll(fetchedExperiments);
        }

        groupRepository.save(experimentGroup);
    }

    public List<Experiment> getGroupedExperiments(String groupName) {
        var group = groupRepository.findByGroupName(groupName);

        if (group == null) {
            return new ArrayList<>();
        }

        return group.getGroupedExperiments().stream().toList();
    }
}
