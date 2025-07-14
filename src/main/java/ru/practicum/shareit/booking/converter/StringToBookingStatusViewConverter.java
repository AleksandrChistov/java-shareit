package ru.practicum.shareit.booking.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.enums.BookingStatusView;
import ru.practicum.shareit.core.error.exception.NotValidException;

@Component
public class StringToBookingStatusViewConverter implements Converter<String, BookingStatusView> {
    @Override
    public BookingStatusView convert(String status) {
        if (status == null || status.isEmpty()) {
            return null;
        }
        try {
            return BookingStatusView.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new NotValidException("Статус не может быть равен " + status);
        }
    }
}
