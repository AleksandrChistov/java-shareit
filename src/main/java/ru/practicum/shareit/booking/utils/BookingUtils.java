package ru.practicum.shareit.booking.utils;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.UpdateBookingDto;
import ru.practicum.shareit.booking.model.Booking;

@UtilityClass
public class BookingUtils {
    public Booking updateBooking(Booking oldBooking, UpdateBookingDto newBookingDto) {
        if (newBookingDto.getStart() != null) {
            oldBooking.setStart(newBookingDto.getStart());
        }

        if (newBookingDto.getEnd() != null) {
            oldBooking.setEnd(newBookingDto.getEnd());
        }

        if (newBookingDto.getStatus() != null) {
            oldBooking.setStatus(newBookingDto.getStatus());
        }

        if (newBookingDto.getFeedback() != null && !newBookingDto.getFeedback().isBlank()) {
            oldBooking.setFeedback(newBookingDto.getFeedback());
        }

        return oldBooking;
    }
}
