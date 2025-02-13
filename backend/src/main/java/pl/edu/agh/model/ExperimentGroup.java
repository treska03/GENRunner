package pl.edu.agh.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Set;

@Document(collection = "group")
public class ExperimentGroup {

    @Id
    private ObjectId id;
    @Indexed(unique = true)
    @Field( name = FieldNames.GROUP_NAME) //nazwa grupy musi byc unikalna
    private String groupName;
    @Field(name = FieldNames.GROUPED_EXPERIMENTS)
    private Set<Experiment> groupedExperiments;

    public ExperimentGroup(ObjectId id, String groupName, Set<Experiment> groupedExperiments) {
        this.id = id;
        this.groupName = groupName;
        this.groupedExperiments = groupedExperiments;
    }

    public ExperimentGroup() {
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Set<Experiment> getGroupedExperiments() {
        return groupedExperiments;
    }

    public void setGroupedExperiments(Set<Experiment> groupedExperiments) {
        this.groupedExperiments = groupedExperiments;
    }

    public static final class FieldNames {
        public static final String GROUP_NAME = "group_name";
        public static final String GROUPED_EXPERIMENTS = "grouped_experiments";
    }
}
