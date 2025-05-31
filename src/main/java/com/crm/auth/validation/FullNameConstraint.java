package com.crm.auth.validation;

import com.crm.auth.validation.validators.FullNameConstraintValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Target(ElementType.TYPE)
@Constraint(validatedBy = FullNameConstraintValidator.class)
@Retention(RUNTIME)
public @interface FullNameConstraint {

    String message() default "First name or last name must be not empty";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
