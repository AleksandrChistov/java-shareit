package ru.practicum.shareit.booking.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Booking {
    @EqualsAndHashCode.Include
    private Long id;
    private final LocalDateTime start;
    private final LocalDateTime end;
    private final Item item;
    private final User booker;
    private final BookingStatus status;
    private final String feedback;
}
