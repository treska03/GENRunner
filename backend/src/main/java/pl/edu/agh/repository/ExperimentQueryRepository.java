package pl.edu.agh.repository;
import org.springframework.data.mongodb.core.query.Query;
import pl.edu.agh.model.Experiment;

import java.util.List;

public interface ExperimentQueryRepository {
    List<Experiment> findByQuery(Query query);
}