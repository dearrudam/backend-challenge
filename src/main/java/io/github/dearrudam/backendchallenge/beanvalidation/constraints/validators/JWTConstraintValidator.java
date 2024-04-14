package io.github.dearrudam.backendchallenge.beanvalidation.constraints.validators;

import io.github.dearrudam.backendchallenge.JwtValidator;
import io.github.dearrudam.backendchallenge.beanvalidation.constraints.JWT;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class JWTConstraintValidator implements ConstraintValidator<JWT, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return JwtValidator.isValid(value);
    }


}
