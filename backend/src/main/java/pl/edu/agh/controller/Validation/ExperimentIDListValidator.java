package pl.edu.agh.controller.Validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import pl.edu.agh.model.Experiment;
import pl.edu.agh.repository.ExperimentRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ExperimentIDListValidator implements ConstraintValidator<ValidExperimentId, List<String>> {
    @Autowired()
    private final ExperimentRepository experimentRepository;

    public ExperimentIDListValidator(ExperimentRepository experimentRepository) {
        this.experimentRepository = experimentRepository;
    }

    @Override
    public boolean isValid(List<String> experimentIds, ConstraintValidatorContext context) {
        if (experimentIds == null || experimentIds.isEmpty()) {
            return false;
        }

        Set<ObjectId> experimentIDInDB = experimentRepository.findExperimentsByIdIn(experimentIds.stream().map(ObjectId::new).toList()).stream().map(Experiment::getId).collect(Collectors.toSet());
        System.out.println(experimentIDInDB);
        for (ObjectId experimentId : experimentIds.stream().map(ObjectId::new).toList()) {
            if (!experimentIDInDB.contains(experimentId)){
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate()+": "+ experimentId).addConstraintViolation();
                return false;
            }
        }
        return true;
    }
}
