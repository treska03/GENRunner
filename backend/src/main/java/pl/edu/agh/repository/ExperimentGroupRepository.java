package pl.edu.agh.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.edu.agh.model.Experiment;
import org.bson.types.ObjectId;
import pl.edu.agh.model.ExperimentGroup;

import java.util.List;

public interface ExperimentGroupRepository extends MongoRepository<ExperimentGroup,ObjectId> {
    ExperimentGroup findByGroupName(String groupName);
    List<ExperimentGroup> findByGroupedExperimentId(ObjectId id);
}
