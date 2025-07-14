package ru.practicum.shareit.core.validation.validid;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = {ValidIdValidator.class})
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidId {

    String message() default "ID не может быть null или меньше 1";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
