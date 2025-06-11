package ru.practicum.shareit.booking.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class BookingRepositoryImpl implements BookingRepository {
    private Long id = 0L;
    private final List<Booking> bookings = new ArrayList<>();

    @Override
    public Optional<Booking> getById(long bookingId) {
        return bookings.stream()
                .filter(b -> b.getId() == bookingId)
                .findFirst();
    }

    @Override
    public Booking create(Booking booking) {
        booking.setId(++id);
        bookings.add(booking);
        return booking;
    }

    @Override
    public Booking update(Booking booking) {
        bookings.add(booking);
        return booking;
    }
}
