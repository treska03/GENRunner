package pl.edu.agh.controller.Validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = RegisteredAlgorithmValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidAlgorithmList {
    String message() default "Invalid algorithm name";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
