package pl.edu.agh.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import pl.edu.agh.model.ExperimentResult;

import java.util.List;
import java.util.Set;

public interface ExperimentResultRepository extends MongoRepository<ExperimentResult, ObjectId> {
    List<ExperimentResult> findByExperimentId(ObjectId experimentId);

    Set<ExperimentResult> findByExperimentIdIn(List<ObjectId> experimentIds);
    void deleteAllByExperimentId(ObjectId experimentId);
}