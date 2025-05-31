package com.crm.auth.validation;

import com.crm.auth.validation.validators.SignUpConstraintValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Target(ElementType.TYPE)
@Constraint(validatedBy = SignUpConstraintValidator.class)
@Retention(RUNTIME)
public @interface SignUpConstraint {

    String message() default "Invalid sign-up request";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
