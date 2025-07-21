package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.enums.BookingStatusView;

import java.util.List;

public interface BookingService {

    BookingDto create(CreateBookingDto bookingDto, Long bookerId);

    BookingDto approve(Long userId, Long bookingId, Boolean approved);

    BookingDto getById(Long userId, Long bookingId);

    List<BookingDto> getAllByBooker(Long userId, BookingStatusView state);

    List<BookingDto> getAllByOwner(Long userId, BookingStatusView state);
}
