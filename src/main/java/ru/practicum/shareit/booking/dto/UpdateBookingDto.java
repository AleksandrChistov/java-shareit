package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.enums.BookingStatus;

import java.time.LocalDateTime;

@Data
public class UpdateBookingDto {
    private Long id;
    private final LocalDateTime start;
    private final LocalDateTime end;
    private final Long itemId;
    private final Long bookerId;
    private final BookingStatus status;
    private final String feedback;
}
