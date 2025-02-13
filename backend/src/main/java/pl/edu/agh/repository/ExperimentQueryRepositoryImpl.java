package pl.edu.agh.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import pl.edu.agh.model.Experiment;

import java.util.List;

@Repository
public class ExperimentQueryRepositoryImpl implements ExperimentQueryRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<Experiment> findByQuery(Query query) {
        return mongoTemplate.find(query, Experiment.class);
    }
}
