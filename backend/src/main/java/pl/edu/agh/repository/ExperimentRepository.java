package pl.edu.agh.repository;

import org.bson.types.ObjectId;
import org.moeaframework.problem.misc.Lis;
import org.springframework.data.mongodb.repository.MongoRepository;
import pl.edu.agh.model.Experiment;
import pl.edu.agh.model.ExperimentStatus;

import java.util.List;
import java.util.Set;

public interface ExperimentRepository extends MongoRepository<Experiment, ObjectId>, ExperimentQueryRepository {
    List<Experiment> findByStatus(ExperimentStatus status);

    Set<Experiment> findExperimentsByIdIn(List<ObjectId> ids);

}
