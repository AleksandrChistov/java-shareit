
package ru.practicum.shareit.booking.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.UpdateBookingDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@UtilityClass
public class BookingMapper {

    public BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem() != null ? booking.getItem().getId() : null,
                booking.getBooker() != null ? booking.getBooker().getId() : null,
                booking.getStatus() != null ? booking.getStatus() : BookingStatus.WAITING,
                booking.getFeedback()
        );
    }

    public Booking toBooking(BookingDto bookingDto, Item item, User booker) {
        return new Booking(
                bookingDto.getId(),
                bookingDto.getStart(),
                bookingDto.getEnd(),
                item,
                booker,
                bookingDto.getStatus(),
                bookingDto.getFeedback()
        );
    }

    public Booking updateBooking(Booking oldBooking, UpdateBookingDto newBookingDto) {
        boolean isStartDateChanged = newBookingDto.getStart() != null;
        boolean isEndDateChanged = newBookingDto.getEnd() != null;
        boolean isStatusChanged = newBookingDto.getStatus() != null;
        boolean isFeedbackChanged = newBookingDto.getFeedback() != null && !newBookingDto.getFeedback().isBlank();

        LocalDateTime start = isStartDateChanged ? newBookingDto.getStart() : oldBooking.getStart();
        LocalDateTime end = isEndDateChanged ? newBookingDto.getEnd() : oldBooking.getEnd();
        BookingStatus status = isStatusChanged ? newBookingDto.getStatus() : oldBooking.getStatus();
        String feedBack = isFeedbackChanged ? newBookingDto.getFeedback() : oldBooking.getFeedback();

        return new Booking(
                oldBooking.getId(),
                start,
                end,
                oldBooking.getItem(),
                oldBooking.getBooker(),
                status,
                feedBack
        );
    }

}
