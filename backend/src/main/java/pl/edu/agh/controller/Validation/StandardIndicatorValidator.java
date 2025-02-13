package pl.edu.agh.controller.Validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.moeaframework.core.indicator.StandardIndicator;

import java.util.List;

public class StandardIndicatorValidator implements ConstraintValidator<ValidMetricsList, List<String >> {
    @Override
    public boolean isValid(List<String> metrics, ConstraintValidatorContext context) {
        if (metrics == null) {
            return true;
        }
        for (String metric: metrics){
            try{StandardIndicator.valueOf(metric);
        } catch (Exception e){
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate() +": "+ metric).addConstraintViolation();
                return false;
            }
        }
        return true;
    }
}
