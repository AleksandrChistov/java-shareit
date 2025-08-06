package ru.practicum.shareit.booking.mapper;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.share.util.DateTimeUtils;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.Instant;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;

class BookingMapperTest {

    @Test
    void toBookingDto_shouldConvertBookingToBookingDto() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        Instant startInstant = start.atZone(java.time.ZoneId.systemDefault()).toInstant();
        Instant endInstant = end.atZone(java.time.ZoneId.systemDefault()).toInstant();

        User booker = new User();
        booker.setId(1L);
        booker.setName("Booker");
        booker.setEmail("booker@example.com");

        Item item = new Item();
        item.setId(1L);
        item.setName("Item");

        UserDto bookerDto = new UserDto(1L, "Booker", "booker@example.com");
        ItemDto itemDto = new ItemDto(1L, "Item", "Description", true, null);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(startInstant);
        booking.setEnd(endInstant);
        booking.setStatus(BookingStatus.WAITING);
        booking.setBooker(booker);
        booking.setItem(item);

        try (MockedStatic<DateTimeUtils> mockedDateTimeUtils = mockStatic(DateTimeUtils.class);
             MockedStatic<UserMapper> mockedUserMapper = mockStatic(UserMapper.class);
             MockedStatic<ItemMapper> mockedItemMapper = mockStatic(ItemMapper.class)) {

            mockedDateTimeUtils.when(() -> DateTimeUtils.toLocalDateTime(booking.getStart())).thenReturn(start);
            mockedDateTimeUtils.when(() -> DateTimeUtils.toLocalDateTime(booking.getEnd())).thenReturn(end);
            mockedUserMapper.when(() -> UserMapper.toUserDto(booker)).thenReturn(bookerDto);
            mockedItemMapper.when(() -> ItemMapper.toItemDto(item)).thenReturn(itemDto);

            BookingDto result = BookingMapper.toBookingDto(booking);

            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals(start, result.getStart());
            assertEquals(end, result.getEnd());
            assertEquals(BookingStatus.WAITING, result.getStatus());
            assertEquals(bookerDto, result.getBooker());
            assertEquals(itemDto, result.getItem());
        }
    }

    @Test
    void toBooking_shouldConvertCreateBookingDtoAndItemAndUserToBooking() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        Instant startInstant = start.atZone(java.time.ZoneId.systemDefault()).toInstant();
        Instant endInstant = end.atZone(java.time.ZoneId.systemDefault()).toInstant();

        CreateBookingDto bookingDto = new CreateBookingDto(1L, start, end);

        Item item = new Item();
        item.setId(1L);
        item.setName("Item");

        User booker = new User();
        booker.setId(1L);
        booker.setName("Booker");

        try (MockedStatic<DateTimeUtils> mockedDateTimeUtils = mockStatic(DateTimeUtils.class)) {
            mockedDateTimeUtils.when(() -> DateTimeUtils.toUTC(start)).thenReturn(startInstant);
            mockedDateTimeUtils.when(() -> DateTimeUtils.toUTC(end)).thenReturn(endInstant);

            Booking result = BookingMapper.toBooking(bookingDto, item, booker);

            assertNotNull(result);
            assertNull(result.getId()); // ID не устанавливается
            assertEquals(startInstant, result.getStart());
            assertEquals(endInstant, result.getEnd());
            assertEquals(item, result.getItem());
            assertEquals(booker, result.getBooker());
            assertEquals(BookingStatus.WAITING, result.getStatus()); // Статус по умолчанию
        }
    }

}