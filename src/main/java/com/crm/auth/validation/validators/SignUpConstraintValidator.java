package com.crm.auth.validation.validators;

import com.crm.auth.dto.request.SignUpRequest;
import com.crm.auth.validation.SignUpConstraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import static java.util.Objects.isNull;
import static org.springframework.util.StringUtils.hasText;

public class SignUpConstraintValidator implements ConstraintValidator<SignUpConstraint, SignUpRequest> {

    @Override
    public boolean isValid(SignUpRequest value, ConstraintValidatorContext context) {
        if (isNull(value.getGeneratePassword())) {
            return false;
        }

        return value.getGeneratePassword() || hasText(value.getPassword());
    }

}
