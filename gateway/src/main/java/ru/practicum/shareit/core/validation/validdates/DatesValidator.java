package ru.practicum.shareit.core.validation.validdates;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.core.error.exception.NotValidException;

import java.time.LocalDateTime;

public class DatesValidator implements ConstraintValidator<ValidDates, Object> {
    @Override
    public boolean isValid(Object object, ConstraintValidatorContext context) {
        if (object instanceof BookingRequestDto bookingDto) {
            LocalDateTime start = bookingDto.getStart();
            LocalDateTime end = bookingDto.getEnd();

            if (start == null || end == null) {
                return false;
            }

            if (start.isBefore(LocalDateTime.now()) || start.isEqual(end)) {
                return false;
            }

            return !start.isAfter(end);
        } else {
            throw new NotValidException("Объект не соответствует CreateBookingDto");
        }
    }
}
