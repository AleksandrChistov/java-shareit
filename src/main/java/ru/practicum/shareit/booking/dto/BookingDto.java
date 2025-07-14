package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.core.validation.validdates.ValidDates;
import ru.practicum.shareit.core.validation.validid.ValidId;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@ValidDates
public class BookingDto {
    private Long id;
    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private final LocalDateTime start;
    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private final LocalDateTime end;
    @ValidId
    private final Long itemId;
    @ValidId
    private final Long bookerId;
    private BookingStatus status;
}
