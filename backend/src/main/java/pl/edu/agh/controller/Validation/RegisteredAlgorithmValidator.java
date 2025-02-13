package pl.edu.agh.controller.Validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.moeaframework.core.spi.AlgorithmFactory;
import org.moeaframework.core.spi.ProviderNotFoundException;
import org.moeaframework.problem.CEC2009.UF1;

import java.util.List;

public class RegisteredAlgorithmValidator implements ConstraintValidator<ValidAlgorithmList, List<String>> {


    @Override
    public void initialize(ValidAlgorithmList constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(List<String> algorithmNames, ConstraintValidatorContext constraintValidatorContext) {

        if (algorithmNames == null) {
            return true;
        }
        for (String algorithmName : algorithmNames) {
            if (!canParseToAlgorithm(algorithmName)) {
                constraintValidatorContext.disableDefaultConstraintViolation();
                constraintValidatorContext.buildConstraintViolationWithTemplate(constraintValidatorContext.getDefaultConstraintMessageTemplate() +": "+ algorithmName).addConstraintViolation();
                return false;
            }
        }
        return true;
    }

    private boolean canParseToAlgorithm(String nameAlgorithm) {
        try {
            AlgorithmFactory.getInstance().getAlgorithm(nameAlgorithm, new UF1());
            return true;
        } catch (ProviderNotFoundException e) {
            return false;
        }
    }

}
