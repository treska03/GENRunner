package pl.edu.agh.controller.Validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.core.spi.ProviderNotFoundException;

import java.util.List;

public class RegisteredProblemValidator implements ConstraintValidator<ValidProblemsList, List<String>>  {
    @Override
    public boolean isValid(List<String> problemNames, ConstraintValidatorContext constraintValidatorContext) {

        if (problemNames == null) {
            return true;
        }
        for (String problemName : problemNames) {
            if (!canParseToProblem(problemName)) {
                constraintValidatorContext.disableDefaultConstraintViolation();
                constraintValidatorContext.buildConstraintViolationWithTemplate(constraintValidatorContext.getDefaultConstraintMessageTemplate() +": "+ problemName).addConstraintViolation();

                return false;
            }
        }
        return true;
    }

    private boolean canParseToProblem(String nameProblem) {
        try {
            ProblemFactory.getInstance().getProblem(nameProblem);
            return true;
        } catch (ProviderNotFoundException e) {
            return false;
        }
    }
}
