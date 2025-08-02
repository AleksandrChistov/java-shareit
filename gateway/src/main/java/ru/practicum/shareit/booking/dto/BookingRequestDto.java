package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.shareit.core.validation.validdates.ValidDates;

import java.time.LocalDateTime;

import static ru.practicum.shareit.item.utils.ItemMessageUtils.NOT_NULL_ITEM_ID_MESSAGE;
import static ru.practicum.shareit.item.utils.ItemMessageUtils.POSITIVE_ITEM_ID_MESSAGE;

@Getter
@AllArgsConstructor
@ValidDates
public class BookingRequestDto {
    @NotNull(message = NOT_NULL_ITEM_ID_MESSAGE)
    @Positive(message = POSITIVE_ITEM_ID_MESSAGE)
    private final Long itemId;
    @NotNull(message = "Дата начала не может быть null")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private final LocalDateTime start;
    @NotNull(message = "Дата окончания не может быть null")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private final LocalDateTime end;
}
