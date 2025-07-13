package ru.practicum.shareit.booking.utils;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.UpdateBookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.time.ZoneOffset;

@UtilityClass
public class BookingUtils {
    public Booking updateBooking(Booking oldBooking, UpdateBookingDto newBookingDto) {
        if (newBookingDto.getStart() != null) {
            oldBooking.setStart(newBookingDto.getStart().toInstant(ZoneOffset.UTC));
        }

        if (newBookingDto.getEnd() != null) {
            oldBooking.setEnd(newBookingDto.getEnd().toInstant(ZoneOffset.UTC));
        }

        if (newBookingDto.getStatus() != null) {
            oldBooking.setStatus(newBookingDto.getStatus());
        }

        return oldBooking;
    }
}
