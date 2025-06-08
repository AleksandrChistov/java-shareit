package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.UpdateBookingDto;

public interface BookingService {

    BookingDto create(BookingDto bookingDto);

    BookingDto update(UpdateBookingDto bookingDto);

}
