
package ru.practicum.shareit.booking.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.share.util.DateTimeUtils;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class BookingMapper {

    public BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                DateTimeUtils.toLocalDateTime(booking.getStart()),
                DateTimeUtils.toLocalDateTime(booking.getEnd()),
                booking.getStatus(),
                UserMapper.toUserDto(booking.getBooker()),
                ItemMapper.toItemDto(booking.getItem())
        );
    }

    public Booking toBooking(CreateBookingDto bookingDto, Item item, User booker) {
        Booking booking = new Booking();
        booking.setStart(DateTimeUtils.toUTC(bookingDto.getStart()));
        booking.setEnd(DateTimeUtils.toUTC(bookingDto.getEnd()));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
        return booking;
    }

}
