package pl.edu.agh.model;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.moeaframework.core.indicator.StandardIndicator;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

public class ExperimentTest {

    @Test
    void testDefaultConstructorAndSetters() {
        // GIVEN
        Experiment experiment = new Experiment();
        ObjectId id = new ObjectId();
        experiment.setId(id);

        // WHEN
        experiment.setAlgorithms(Set.of("VEGA", "AMOSA"));
        experiment.setProblems(Set.of("ZDT1", "ZDT2"));
        experiment.setMetrics(Set.of(StandardIndicator.InvertedGenerationalDistancePlus, StandardIndicator.Hypervolume));
        experiment.setBudget(1000);
        experiment.setStatus(ExperimentStatus.PENDING);

        Instant now = Instant.now();
        experiment.setStartTime(now);
        experiment.setEndTime(now.plusSeconds(3600));
        experiment.setErrorMessage("Some error");

        // THEN
        Assertions.assertEquals(id, experiment.getId());
        Assertions.assertEquals(2, experiment.getAlgorithms().size());
        Assertions.assertEquals(2, experiment.getProblems().size());
        Assertions.assertEquals(2, experiment.getMetrics().size());
        Assertions.assertEquals(1000, experiment.getBudget());
        Assertions.assertEquals(ExperimentStatus.PENDING, experiment.getStatus());
        Assertions.assertEquals(now, experiment.getStartTime());
        Assertions.assertEquals(now.plusSeconds(3600), experiment.getEndTime());
        Assertions.assertEquals("Some error", experiment.getErrorMessage());
    }

    @Test
    void testConstructorWithParameters() {
        // GIVEN
        ObjectId id = new ObjectId();

        // WHEN
        Experiment experiment = new Experiment(
                id,
                Set.of("VEGA"),
                Set.of("ZDT1"),
                Set.of(StandardIndicator.R1Indicator),
                500,
                1,
                ExperimentStatus.RUNNING,
                Instant.now(),
                null,
                null
        );

        // THEN
        // Konstruktor w kodzie źródłowym ignoruje część parametrów i sam ustawia status i startTime
        // więc sprawdzamy czy to rzeczywiście jest RUNNING i startTime nie jest null
        Assertions.assertNotNull(experiment.getStartTime());
        Assertions.assertNull(experiment.getEndTime());
        Assertions.assertEquals(ExperimentStatus.RUNNING, experiment.getStatus());
    }
}
