package ru.practicum.shareit.booking.dao;

import ru.practicum.shareit.booking.model.Booking;

import java.util.Optional;

public interface BookingRepository {

    Optional<Booking> getById(long bookingId);

    Booking create(Booking booking);

    Booking update(Booking booking);

}
