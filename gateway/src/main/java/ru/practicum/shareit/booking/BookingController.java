package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.enums.BookingState;

import static ru.practicum.shareit.booking.utils.BookingMessageUtils.NOT_NULL_BOOKING_ID_MESSAGE;
import static ru.practicum.shareit.booking.utils.BookingMessageUtils.POSITIVE_BOOKING_ID_MESSAGE;
import static ru.practicum.shareit.share.constant.HttpHeadersConstants.X_SHARER_USER_ID;
import static ru.practicum.shareit.user.utils.UserMessageUtils.NOT_NULL_USER_ID_MESSAGE;
import static ru.practicum.shareit.user.utils.UserMessageUtils.POSITIVE_USER_ID_MESSAGE;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> create(
            @RequestHeader(value = X_SHARER_USER_ID) @NotNull(message = NOT_NULL_USER_ID_MESSAGE)
            @Positive(message = POSITIVE_USER_ID_MESSAGE) Long userId,
            @RequestBody @Valid BookingRequestDto requestDto
    ) {
        log.info("Create booking {}, userId={}", requestDto, userId);
        return bookingClient.bookItem(userId, requestDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approve(
            @RequestHeader(value = X_SHARER_USER_ID) @NotNull(message = NOT_NULL_USER_ID_MESSAGE)
            @Positive(message = POSITIVE_USER_ID_MESSAGE) Long userId,
            @PathVariable @NotNull(message = NOT_NULL_BOOKING_ID_MESSAGE)
            @Positive(message = POSITIVE_BOOKING_ID_MESSAGE) Long bookingId,
            @RequestParam(required = false) Boolean approved
    ) {
        log.info("Approve booking {}, userId={}, approved={}", bookingId, userId, approved);
        return bookingClient.patchBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getById(
            @RequestHeader(value = X_SHARER_USER_ID) @NotNull(message = NOT_NULL_USER_ID_MESSAGE)
            @Positive(message = POSITIVE_USER_ID_MESSAGE) Long userId,
            @PathVariable @NotNull(message = NOT_NULL_BOOKING_ID_MESSAGE)
            @Positive(message = POSITIVE_BOOKING_ID_MESSAGE) Long bookingId
    ) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByBooker(
            @RequestHeader(value = X_SHARER_USER_ID) @NotNull(message = NOT_NULL_USER_ID_MESSAGE)
            @Positive(message = POSITIVE_USER_ID_MESSAGE) Long userId,
            @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size
    ) {
        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getBookings(userId, getBookingStatusView(stateParam), from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllByOwner(
            @RequestHeader(value = X_SHARER_USER_ID) @NotNull(message = NOT_NULL_USER_ID_MESSAGE)
            @Positive(message = POSITIVE_USER_ID_MESSAGE) Long userId,
            @RequestParam(name = "state", defaultValue = "ALL") String state
    ) {
        log.info("Get bookings with state={}, by owner {}", state, userId);
        return bookingClient.getBookingsByOwner(userId, getBookingStatusView(state));
    }

    private BookingState getBookingStatusView(String state) {
        return BookingState.fromString(state).orElse(BookingState.ALL);
    }

}
