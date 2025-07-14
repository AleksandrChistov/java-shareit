package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingStatusView;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.core.validation.validid.ValidId;

import java.util.List;

import static ru.practicum.shareit.share.constant.HttpHeadersConstants.X_SHARER_USER_ID;
import static ru.practicum.shareit.user.utils.UserUtils.INVALID_USER_ID_MESSAGE;

@RestController
@RequestMapping(path = "/bookings", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto create(@Valid @RequestBody BookingDto bookingDto) {
        return bookingService.create(bookingDto);
    }

    @PatchMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public void approve(
            @RequestHeader(value = X_SHARER_USER_ID, required = false) @ValidId(message = INVALID_USER_ID_MESSAGE) Long userId,
            @PathVariable @ValidId Long bookingId,
            @RequestParam(required = false) Boolean approved
    ) {
        bookingService.approve(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingDto getById(
            @RequestHeader(value = X_SHARER_USER_ID, required = false) @ValidId(message = INVALID_USER_ID_MESSAGE) Long userId,
            @PathVariable @ValidId Long bookingId
    ) {
        return bookingService.getById(userId, bookingId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<BookingDto> getAllByBooker(
            @RequestHeader(value = X_SHARER_USER_ID, required = false) @ValidId(message = INVALID_USER_ID_MESSAGE) Long userId,
            @RequestParam(name = "state", defaultValue = "ALL") BookingStatusView state
    ) {
        return bookingService.getAllByBooker(userId, state);
    }

    @GetMapping("/owner")
    @ResponseStatus(HttpStatus.OK)
    public List<BookingDto> getAllByOwner(
            @RequestHeader(value = X_SHARER_USER_ID, required = false) @ValidId(message = INVALID_USER_ID_MESSAGE) Long userId,
            @RequestParam(name = "state", defaultValue = "ALL") BookingStatusView state
    ) {
        return bookingService.getAllByOwner(userId, state);
    }

}
