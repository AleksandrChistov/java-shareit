package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.enums.BookingStatusView;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

import static ru.practicum.shareit.share.constant.HttpHeadersConstants.X_SHARER_USER_ID;

@RestController
@RequestMapping(path = "/bookings", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto create(
            @RequestHeader(value = X_SHARER_USER_ID) Long userId,
            @RequestBody CreateBookingDto bookingDto
    ) {
        return bookingService.create(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingDto approve(
            @RequestHeader(value = X_SHARER_USER_ID) Long userId,
            @PathVariable Long bookingId,
            @RequestParam(required = false) Boolean approved
    ) {
        return bookingService.approve(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingDto getById(
            @RequestHeader(value = X_SHARER_USER_ID) Long userId,
            @PathVariable Long bookingId
    ) {
        return bookingService.getById(userId, bookingId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<BookingDto> getAllByBooker(
            @RequestHeader(value = X_SHARER_USER_ID) Long userId,
            @RequestParam(name = "state", defaultValue = "ALL") String state
    ) {
        return bookingService.getAllByBooker(userId, getBookingStatusView(state));
    }

    @GetMapping("/owner")
    @ResponseStatus(HttpStatus.OK)
    public List<BookingDto> getAllByOwner(
            @RequestHeader(value = X_SHARER_USER_ID) Long userId,
            @RequestParam(name = "state", defaultValue = "ALL") String state
    ) {
        return bookingService.getAllByOwner(userId, getBookingStatusView(state));
    }

    private BookingStatusView getBookingStatusView(String state) {
        BookingStatusView stateView = BookingStatusView.fromString(state);
        return stateView == null ? BookingStatusView.ALL : stateView;
    }

}
