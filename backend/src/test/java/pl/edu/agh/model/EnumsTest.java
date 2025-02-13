package pl.edu.agh.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EnumsTest {

    @Test
    void testExperimentStatusEnum() {
        // GIVEN
        ExperimentStatus status = ExperimentStatus.RUNNING;

        // THEN
        Assertions.assertEquals("RUNNING", status.name());

        // WHEN
        ExperimentStatus[] values = ExperimentStatus.values();

        // THEN
        Assertions.assertTrue(values.length > 0);
    }
}
