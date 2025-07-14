package ru.practicum.shareit.core.validation.validdates;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;

public class DatesValidator implements ConstraintValidator<ValidDates, Object> {

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext context) {
        if (object == null) {
            return false;
        }

        try {
            LocalDateTime start = (LocalDateTime) object.getClass().getMethod("getStart").invoke(object);
            LocalDateTime end = (LocalDateTime) object.getClass().getMethod("getEnd").invoke(object);

            if (start == null || end == null) {
                return false;
            }

            if (start.isBefore(LocalDateTime.now())) {
                return false;
            }

            return !start.isAfter(end);
        } catch (Exception e) {
            throw new RuntimeException("У объектов нет методов getStart и getEnd");
        }
    }
}
