package ru.practicum.shareit.core.validation.validdates;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = {DatesValidator.class})
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDates {

    String message() default "Дата начала бронирования не может быть в прошлом или раньше даты окончания";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
