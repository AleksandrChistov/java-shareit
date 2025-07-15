package ru.practicum.shareit.booking.view;

import java.time.Instant;

public interface BookingDatesView {
    Long getItemId();

    Instant getLastBookingDate();

    Instant getNextBookingDate();
}
