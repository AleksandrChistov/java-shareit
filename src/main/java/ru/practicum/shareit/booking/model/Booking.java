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
    private LocalDateTime start;
    private LocalDateTime end;
    private Item item;
    private User booker;
    private BookingStatus status;
    private String feedback;

    public Booking(LocalDateTime start, LocalDateTime end, Item item, User booker, BookingStatus status, String feedback) {
        this.start = start;
        this.end = end;
        this.item = item;
        this.booker = booker;
        this.status = status;
        this.feedback = feedback;
    }
}
