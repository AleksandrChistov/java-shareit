package ru.practicum.shareit.core.validation.validid;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidIdValidator implements ConstraintValidator<ValidId, Long> {
    @Override
    public boolean isValid(Long value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return value >= 1;
    }
}
