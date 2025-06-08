package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.UpdateBookingDto;
import ru.practicum.shareit.booking.service.BookingService;

@RestController
@RequestMapping(path = "/bookings", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto create(@Valid @RequestBody BookingDto bookingDto) {
        return bookingService.create(bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto update(@Valid @RequestBody UpdateBookingDto bookingDto) {
        return bookingService.update(bookingDto);
    }

}
