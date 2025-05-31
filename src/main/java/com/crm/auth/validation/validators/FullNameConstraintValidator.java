package com.crm.auth.validation.validators;


import com.crm.auth.dto.request.SignUpRequest;
import com.crm.auth.validation.FullNameConstraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import static io.jsonwebtoken.lang.Strings.hasText;

public class FullNameConstraintValidator
        implements ConstraintValidator<FullNameConstraint, SignUpRequest> {

    @Override
    public boolean isValid(SignUpRequest value, ConstraintValidatorContext context) {
        return hasText(value.getFirstName()) || hasText(value.getLastName());
    }

}
